package com.mad.achatz.fa_todo;


import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TodoWebAccess {

    private static final String URL = "http://10.0.2.2:8080/api/todos/";

    private TodoWebAccessListener listener;

    // Um festzustellen, ob eine Verbindung hergestellt werden kann.
    // Wird beim ersten fehlgeschlagenen Versuch auf true gesetzt.
    private boolean noConnection = false;

    public TodoWebAccess(TodoWebAccessListener listener) {
        this.listener = listener;
    }

    public void getAllItems() {
        if (noConnection) return;
        new AsyncTask<Void, Void, List<ToDo>>() {
            @Override
            protected List<ToDo> doInBackground(Void... params) {
                return getAllToDosForAsync();
            }

            @Override
            protected void onPostExecute(List<ToDo> toDoList) {
                if (toDoList != null) listener.OnTodosRetrieved(toDoList);
                else notifyNoConnection();
            }
        }.execute();
    }

    public void deleteTodo(final ToDo todo) {
        if (noConnection) return;
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return createUpdateDeleteForAsync("DELETE", todo);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                // Result ist nur false wenn keine Verbindung aufgebaut werden kann.
                // Es ist unabhängig, davon ob ein todo gelöscht wurde oder nicht
                if (!result) notifyNoConnection();
            }
        }.execute();
    }

    public void clearWebDatabase() {
        if (noConnection) return;
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                boolean result = true;
                List<ToDo> todoList = getAllToDosForAsync();
                if (todoList == null) return false;
                for (ToDo t : todoList) {
                    result = result && createUpdateDeleteForAsync("DELETE", t);
                }
                return result;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (!result) notifyNoConnection();
            }
        }.execute();
    }

    public void createTodo(final ToDo todo) {
        if (noConnection) return;
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return createUpdateDeleteForAsync("POST", todo);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                // Result ist nur false wenn keine Verbindung aufgebaut werden kann.
                // Es ist unabhängig, davon ob ein todo erstellt wurde oder nicht
                if (!result) notifyNoConnection();
            }
        }.execute();
    }


    public void updateTodo(final ToDo todo) {
        if (noConnection) return;
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return createUpdateDeleteForAsync("PUT", todo);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                // Result ist nur false wenn keine Verbindung aufgebaut werden kann.
                // Es ist unabhängig, davon ob ein todo geändert wurde oder nicht
                if (!result) notifyNoConnection();
            }
        }.execute();
    }

    public void checkConnection(final ProgressBar progressBar) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(URL).openConnection();
                    connection.setConnectTimeout(5000);
                    connection.getInputStream();
                    return (connection.getResponseCode() == HttpURLConnection.HTTP_OK);
                } catch (IOException e) {
                    return false;
                }
            }

            @Override
            protected void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                progressBar.setVisibility(View.GONE);
                if (result)
                    listener.OnConnectionSuccess();
                else
                    notifyNoConnection();
            }
        }.execute();
    }

    private ArrayList<ToDo> getAllToDosForAsync() {
        ArrayList<ToDo> todoList = new ArrayList<>();
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(URL).openConnection();
            connection.setConnectTimeout(5000);
            InputStream inputStream = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                connection.disconnect();
                return null;
            }
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readValue(inputStream, JsonNode.class);

            for (JsonNode node : rootNode) {
                todoList.add(mapper.treeToValue(node, ToDo.class));
            }

            connection.disconnect();

        } catch (IOException e) {
            todoList = null;
        }
        return todoList;
    }

    private boolean createUpdateDeleteForAsync(String method, ToDo toDo) {
        boolean result = true;
        try {
            String url = URL + toDo.getDbId();
            if (method.equals("POST"))
                url = URL;

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(5000);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod(method);

            if (method.equals("POST") || method.equals("PUT")) {
                connection.setDoOutput(true);
                OutputStream outputStream = connection.getOutputStream();
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(outputStream, toDo);
            }

            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                result = false;
            }

            connection.disconnect();

        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    public void notifyNoConnection() {
        noConnection = true;
        listener.OnNoConnectionAvailable();
    }

    public interface TodoWebAccessListener {
        void OnTodosRetrieved(List<ToDo> toDoList);

        void OnConnectionSuccess();

        void OnNoConnectionAvailable();
    }

}
