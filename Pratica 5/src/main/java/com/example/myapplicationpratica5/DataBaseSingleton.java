package com.example.myapplicationpratica5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public final class DataBaseSingleton {

    private static final String DB_NAME = "checkin_db";
    private static DataBaseSingleton INSTANCE;

    private SQLiteDatabase db;

    private static final String[] SCRIPT_DATABASE_CREATE = new String[] {
            "CREATE TABLE Categoria (" +
                    "idCategoria INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nome TEXT NOT NULL);",

            "CREATE TABLE Checkin (" +
                    "Local TEXT PRIMARY KEY, " +
                    "qtdVisitas INTEGER NOT NULL, " +
                    "cat INTEGER NOT NULL, " +
                    "latitude TEXT NOT NULL, " +
                    "longitude TEXT NOT NULL, " +
                    "CONSTRAINT fkey0 FOREIGN KEY (cat) REFERENCES Categoria (idCategoria));",

            "INSERT INTO Categoria (nome) VALUES ('Restaurante');",
            "INSERT INTO Categoria (nome) VALUES ('Bar');",
            "INSERT INTO Categoria (nome) VALUES ('Cinema');",
            "INSERT INTO Categoria (nome) VALUES ('Universidade');",
            "INSERT INTO Categoria (nome) VALUES ('Estádio');",
            "INSERT INTO Categoria (nome) VALUES ('Parque');",
            "INSERT INTO Categoria (nome) VALUES ('Religioso');",
            "INSERT INTO Categoria (nome) VALUES ('Outros');"
    };

    private DataBaseSingleton(Context ctx) {
        db = ctx.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='Categoria'", null);

        if (c != null) {
            boolean categoriasExistem = c.getCount() > 0;
            c.close();

            if (!categoriasExistem) {
                criarBancoEDadosIniciais();
                inserirLocalInicial();
            }
        }
    }

    private void criarBancoEDadosIniciais() {
        for (String script : SCRIPT_DATABASE_CREATE) {
            db.execSQL(script);
        }
    }

    private void inserirLocalInicial() {
        Cursor catCursor = db.query(
                "Categoria",
                new String[]{"idCategoria"},
                "nome = ?",
                new String[]{"Bar"},
                null, null, null);

        if (catCursor != null && catCursor.moveToFirst()) {
            int idBar = catCursor.getInt(catCursor.getColumnIndexOrThrow("idCategoria"));
            catCursor.close();

            ContentValues vals = new ContentValues();
            vals.put("Local", "Bar do Leão");
            vals.put("qtdVisitas", 0);
            vals.put("cat", idBar);
            vals.put("latitude", "-23.550520");
            vals.put("longitude", "-46.633308");

            insert("Checkin", vals);
        }
    }

    public static synchronized DataBaseSingleton getInstance(Context ctx) {
        if (INSTANCE == null) {
            INSTANCE = new DataBaseSingleton(ctx.getApplicationContext());
        }
        INSTANCE.open(ctx);
        return INSTANCE;
    }

    private void open(Context ctx) {
        if (db == null || !db.isOpen()) {
            db = ctx.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
        }
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    public long insert(String tabela, ContentValues valores) {
        return db.insert(tabela, null, valores);
    }

    public int update(String tabela, ContentValues valores, String whereClause, String[] whereArgs) {
        return db.update(tabela, valores, whereClause, whereArgs);
    }

    public int delete(String tabela, String whereClause, String[] whereArgs) {
        return db.delete(tabela, whereClause, whereArgs);
    }

    public Cursor search(String tabela, String[] colunas, String whereClause, String[] whereArgs, String orderBy) {
        return db.query(tabela, colunas, whereClause, whereArgs, null, null, orderBy);
    }

    public void resetDatabase(Context ctx) {
        close();
        ctx.deleteDatabase(DB_NAME);
    }
}
