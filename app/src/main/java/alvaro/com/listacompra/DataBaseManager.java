package alvaro.com.listacompra;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Alvaro on 15/11/2015.
 */
public class DataBaseManager
{
    public static final String NOMBRE_TABLA = "productos"; //NOMBRE DE LA TABLA

    public static final String ID_PRODUCTO = "_id";
    public static final String NOMBRE_PRODUCTO = "nombre";
    public static final String CANTIDAD_PRODUCTO = "cantidad";
    public static final String DESCRIPCION_PRODUCTO = "descripcion";
    public static final String ESTADO_PRODUCTO = "estado"; //*****************************
    public static final String PRECIO_PRODUCTO = "precio";

    /**
     * CAMPO PARA CREAR LA TABLA
     */

    public static final String CREAR_TABLA = "create table "+NOMBRE_TABLA+"("
                                                +ID_PRODUCTO+" integer primary key autoincrement,"
                                                +NOMBRE_PRODUCTO+" text not null,"
                                                +CANTIDAD_PRODUCTO+" integer,"
                                                +DESCRIPCION_PRODUCTO+" text,"
                                                +PRECIO_PRODUCTO+" real,"
                                                +ESTADO_PRODUCTO+" integer);"; //*****************************

    DataBaseHelper dbHelper;
    private SQLiteDatabase db;

    /**
     * Constructor
      * @param context
     */
    public DataBaseManager(Context context)
    {
        dbHelper = new DataBaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    //METODO INSERTAR

    /**
     * Metodo Insertar
     * @param nombre
     * @param cantidad
     * @param descripcion
     * @param precio
     * @return devuelve un long
     */
    public long insertar(String nombre, int cantidad, String descripcion, double precio)
    {

        ContentValues contentValues = new ContentValues();
        contentValues.put(NOMBRE_PRODUCTO, nombre.toLowerCase()); //convertimos a minuscula
        contentValues.put(CANTIDAD_PRODUCTO, cantidad);
        contentValues.put(DESCRIPCION_PRODUCTO, descripcion);
        contentValues.put(PRECIO_PRODUCTO, precio);
        contentValues.put(ESTADO_PRODUCTO, 0); //Insertamos un 0 (false) *************************
        

        return db.insert(NOMBRE_TABLA, null, contentValues);
    }

    /**
     * Metodo que devuelve todos los items
     * @return devuelve un cursor
     */
    public Cursor getItems()
    {
        String[] producto = new String[] {ID_PRODUCTO,NOMBRE_PRODUCTO,CANTIDAD_PRODUCTO, DESCRIPCION_PRODUCTO, PRECIO_PRODUCTO, ESTADO_PRODUCTO}; //******************************

        return db.query(NOMBRE_TABLA, producto, null, null, null, null, null);
    }

    /**
     * Metodo que elimina un item
     * @param item
     * @return devuelve un booleano
     */
    public boolean eliminar(int item)
    {
        try
        {
            db.delete(NOMBRE_TABLA, ID_PRODUCTO + "=?", new String[]{Integer.toString(item)});
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Metodo que elimina todos los items
     * @return devuelve un booleano
     */
    public boolean eliminarTodo()
    {
        try
        {
            db.execSQL("delete from " + NOMBRE_TABLA);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Metodo que modifica un item
     * @param id
     * @param nombre
     * @param cant
     * @param desc
     * @param precio
     * @return devuelve un booleano
     */
    public boolean modificar(int id, String nombre, int cant, String desc, double precio)
    {
        ContentValues valores = new ContentValues();
        valores.put(NOMBRE_PRODUCTO, nombre);
        valores.put(CANTIDAD_PRODUCTO, cant);
        valores.put(DESCRIPCION_PRODUCTO, desc);
        valores.put(PRECIO_PRODUCTO, precio);

        try
        {
            db.update(NOMBRE_TABLA, valores, ID_PRODUCTO + "=?", new String[]{Integer.toString(id)});
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Metodo que modifica los estados del checkbox
     * @param id
     * @param estado
     */
    public void modificarEstado(int id, int estado)
    {
        ContentValues valores = new ContentValues();
        valores.put(ESTADO_PRODUCTO, estado);

        db.update(NOMBRE_TABLA, valores, ID_PRODUCTO + "=?", new String[]{Integer.toString(id)});
    }
}
