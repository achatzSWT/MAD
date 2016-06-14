package com.mad.achatz.fa_todo;


import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class TodoWebAccess {

    private static final String URL = "http://10.0.2.2:8080/api/todos/";

    private TodoWebAccessListener listener;

    // Um festzustellen, ob eine Verbindung hergestellt werden kann.
    // Wird beim ersten fehlgeschlagenen Versuch auf true gesetzt.
    public boolean connectionAvailable = false;

    public TodoWebAccess(TodoWebAccessListener listener) {
        this.listener = listener;
    }

    public void checkConnection(ProgressDialog progressDialog) {
        new CheckConnectionTask(progressDialog).execute();
    }

    public void getAllItems(ProgressDialog progressDialog) {
        if (connectionAvailable)
            new WebAccessTask("GET", progressDialog).execute();
    }

    public void deleteTodo(final ToDo todo) {
        if (connectionAvailable)
            new WebAccessTask("DELETE", null).execute(todo);
    }

    public void replaceWebDatabase(List<ToDo> toDoList, ProgressDialog progressDialog) {
        if (toDoList == null)
            toDoList = new ArrayList<>();
        if (connectionAvailable)
            new WebAccessTask("DELETEALL", progressDialog).execute(toDoList.toArray(new ToDo[toDoList.size()]));
    }

    public void createTodo(final ToDo todo) {
        if (connectionAvailable)
            new WebAccessTask("POST", null).execute(todo);
    }

    public void updateTodo(final ToDo todo) {
        if (connectionAvailable)
            new WebAccessTask("PUT", null).execute(todo);
    }

    public void notifyNoConnection() {
        connectionAvailable = false;
        listener.OnConnectionLost();
    }

    private class WebAccessTask extends AsyncTask<ToDo, Void, Boolean> {

        private String method;

        private ArrayList<ToDo> todoList = new ArrayList<>();
        private ProgressDialog progressDialog;

        WebAccessTask(String method, ProgressDialog progressDialog) {
            this.method = method;
            this.progressDialog = progressDialog;
        }

        @Override
        protected void onPreExecute() {
            if (progressDialog != null) progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(ToDo... params) {
            boolean result = true;
            if (method.equals("DELETEALL")) {
                // Finde alle Todos in Datenbank
                result = doAsyncConnectionTask("GET", null);
                // Jetzt lösche diese Todos
                for (ToDo t : todoList) {
                    result = result && doAsyncConnectionTask("DELETE", t);
                }
                // Wenn params übergeben worden, dann sollen diese jetzt hinzugefügt werden.
                for (ToDo t : params) {
                    result = result && doAsyncConnectionTask("POST", t);
                }
            } else {
                if (params.length > 0) {
                    for (ToDo t : params) {
                        result = result && doAsyncConnectionTask(method, t);
                    }
                } else {
                    result = doAsyncConnectionTask(method, null);
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (progressDialog != null) progressDialog.dismiss();
            if (!result) {
                notifyNoConnection();
                return;
            }

            if (method.equals("GET"))
                listener.OnTodosRetrieved(todoList);
        }

        private boolean doAsyncConnectionTask(String method, ToDo todo) {
            boolean result = true;
            try {
                String url = URL;
                if (todo != null && (method.equals("PUT") || method.equals("DELETE")))
                    url += todo.getDbId();

                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestMethod(method);

                if (method.equals("POST") || method.equals("PUT")) {
                    connection.setDoOutput(true);
                    OutputStream outputStream = connection.getOutputStream();
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.writeValue(outputStream, todo);
                }

                InputStream inputStream = connection.getInputStream();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    result = false;
                }

                if (method.equals("GET")) {
                    todoList.clear();
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode rootNode = mapper.readValue(inputStream, JsonNode.class);
                    for (JsonNode node : rootNode) {
                        todoList.add(mapper.treeToValue(node, ToDo.class));
                    }
                }

                connection.disconnect();

            } catch (IOException e) {
                result = false;
            }

            return result;
        }

    }

    private class CheckConnectionTask extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog progressDialog;

        public CheckConnectionTask(ProgressDialog progressDialog) {
            this.progressDialog = progressDialog;
        }

        @Override
        protected void onPreExecute() {
            if (progressDialog != null) {
                progressDialog.show();
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            SocketAddress socketAddress;
            try {
                socketAddress = new InetSocketAddress(Inet4Address.getByName("10.0.2.2"), 8080);
            } catch (UnknownHostException e) {
                return false;
            }
            Socket socket = new Socket();
            try {
                int timeout = 3000;
                socket.connect(socketAddress, timeout);
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (progressDialog != null) {
                progressDialog.hide();
            }
            if (result != null) {
                connectionAvailable = result;
                listener.OnConnectionAvailableResult(result);
            }
        }
    }


    public interface TodoWebAccessListener {
        void OnTodosRetrieved(List<ToDo> toDoList);

        void OnConnectionAvailableResult(boolean connectionAvailable);

        void OnConnectionLost();
    }

}
