package com.betosoft.databaseapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.betosoft.database.DatabaseHelper;


public class MainActivity extends AppCompatActivity {
    private static final int ADD_ID = Menu.FIRST + 1;
    private static final int DELETE_ID = Menu.FIRST + 3;

    private ListView lvwConstante;
    private DatabaseHelper db = null;
    private Cursor constantsCursor = null;
    private final String[] from = { DatabaseHelper.TITLE, DatabaseHelper.VALUE };
    private final int[] to = { R.id.txtTitle, R.id.txtValue };
    private ListAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        constantsCursor = db.getReadableDatabase().rawQuery("SELECT _ID,title,value FROM constants ORDER BY title", null);
        adapter = new SimpleCursorAdapter(this, R.layout.row, constantsCursor, from, to, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        lvwConstante = findViewById(R.id.lvwConstante);
        lvwConstante.setAdapter(adapter);

        registerForContextMenu(lvwConstante);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(Menu.NONE,ADD_ID, Menu.NONE, "Agregar...");
        menu.add(Menu.NONE,DELETE_ID, Menu.NONE,"Borrar...");

        super.onCreateContextMenu(menu, v, menuInfo);
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == ADD_ID) {
            add();
        }
        if (item.getItemId() == DELETE_ID) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            delete(info.id);
        }
        return super.onContextItemSelected(item);
    }

    public static class DialogWraper {
        private EditText edtConstante;
        private EditText edtValor;
        private View base;

        public DialogWraper(View base) {
            this.base = base;
        }

        public EditText getEdtConstante() {
            edtConstante = base.findViewById(R.id.edtConstante);
            return edtConstante;
        }

        public EditText getEdtValor() {
            edtValor = base.findViewById(R.id.edtValor);
            return edtValor;
        }

        public String getConstante() {
            return getEdtConstante().getText().toString();
        }

        public float getValor() {
            return Float.parseFloat(getEdtValor().getText().toString());
        }
    }

    private void add() {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.add_edit, null);
        final DialogWraper dialogWraper = new DialogWraper(view);

        new AlertDialog.Builder(this)
                .setTitle(R.string.add)
                .setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        processAdd(dialogWraper);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // No hacemos nada
                    }
                }).show();
    }

    private void delete(final long rowID) {
        if (rowID != 0) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.delete)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            processDelete(rowID);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // No hacemos nada
                        }
                    }).show();
        }
    }

    private void processAdd(DialogWraper dialogWraper) {
        ContentValues contentValues = new ContentValues(2);
        contentValues.put(DatabaseHelper.TITLE, dialogWraper.getConstante());
        contentValues.put(DatabaseHelper.VALUE, dialogWraper.getValor());
        db.getWritableDatabase().insert("constants", null, contentValues);
        constantsCursor = db.getReadableDatabase().rawQuery("SELECT _ID,title,value FROM constants ORDER BY title", null);
        adapter = new SimpleCursorAdapter(this, R.layout.row, constantsCursor, from, to, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        lvwConstante.setAdapter(adapter);
    }

    private void processDelete(long rowID) {
        String[] args = { String.valueOf(rowID) };
        db.getReadableDatabase().delete("constants", "_ID=?", args);
        constantsCursor = db.getReadableDatabase().rawQuery("SELECT _ID,title,value FROM constants ORDER BY title", null);
        adapter = new SimpleCursorAdapter(this, R.layout.row, constantsCursor, from, to, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        lvwConstante.setAdapter(adapter);
    }

}