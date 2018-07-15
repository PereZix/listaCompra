package alvaro.com.listacompra;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Alvaro on 15/11/2015.
 */

public class DataBaseHelper extends SQLiteOpenHelper
{
    private static final String DB_NOMBRE = "listaCompra.sqlite"; //NOMBRE DE LA BASE DE DATOS
    private static final int DB_VERSION = 1; //VERSION DE LA TABLA

    /**
     * Constructor
     * @param context
     */
    public DataBaseHelper(Context context)
    {
        super(context, DB_NOMBRE, null, DB_VERSION);
    }

    /**
     * Metodo que crea la clase DataBaseHelper
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(DataBaseManager.CREAR_TABLA); //CREAMOS LA TABLA
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
