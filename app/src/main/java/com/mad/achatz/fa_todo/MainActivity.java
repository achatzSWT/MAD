package com.mad.achatz.fa_todo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    ToDoListFragment toDoListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toDoListFragment = (ToDoListFragment) getSupportFragmentManager().findFragmentById(R.id.todo_list_fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.menu_sort_date_fav:
                toDoListFragment.setSortMethod(ToDoListFragment.SORT_DATE_FAV);
                return true;
            case R.id.menu_sort_fav_date:
                toDoListFragment.setSortMethod(ToDoListFragment.SORT_FAV_DATE);
                return true;
        }

        return false;
    }

    public void addToDoFabClicked(View view) {
        ((ToDoListFragment) getSupportFragmentManager().findFragmentById(R.id.todo_list_fragment)).startAddTodoActivity();
    }

}
