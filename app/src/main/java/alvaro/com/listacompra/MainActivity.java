package alvaro.com.listacompra;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Clase MainActivity
 */
public class MainActivity extends AppCompatActivity
{
    private DataBaseManager manager;
    private Cursor cursor;
    Cursor cursorRepes;
    private ArrayList<Producto> lista = new ArrayList<Producto>();
    private AdapterProducto adpPro;
    public ListView listaPro;
    private Double cantTotal = 0.0;
    ArrayList<Double> arrayCantidades = new ArrayList<>();

    /**
     * Metodo onCreate del MainActivity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //INSTANCIAMOS LA CLASE DATABASEMANAGER
        manager = new DataBaseManager(this);
        listaPro = (ListView)findViewById(R.id.listView);
        adpPro = new AdapterProducto(this,R.layout.producto_layout,lista,getLayoutInflater());

        try
        {
            cargarDatos();
            registerForContextMenu(listaPro);
        }
        catch (Exception e)
        {
            showMessage(R.string.errorCargarDatos);
        }
    }

    /**
     * Metodo CargarDatos. Sirve para cargar los datos atraves de un cursor
     */
    private void cargarDatos()
    {
        cursor = manager.getItems();
        lista.clear();
        arrayCantidades.clear(); //Limpiar lista de precios

        while (cursor.moveToNext())
        {
            Producto item = new Producto();
            item._id = cursor.getInt(cursor.getColumnIndex(manager.ID_PRODUCTO));
            item._nombre = cursor.getString(cursor.getColumnIndex(manager.NOMBRE_PRODUCTO));
            item._cantidad = cursor.getInt(cursor.getColumnIndex(manager.CANTIDAD_PRODUCTO));
            item._descripcion = cursor.getString(cursor.getColumnIndex(manager.DESCRIPCION_PRODUCTO));
            item._precio = cursor.getDouble(cursor.getColumnIndex(manager.PRECIO_PRODUCTO));
            item._estado = cursor.getInt(cursor.getColumnIndex(manager.ESTADO_PRODUCTO));

            arrayCantidades.add(item._precio); //Guardamos los precios

            lista.add(item);
        }

        cursor.close();
        //manager.dbHelper.close();
        listaPro.setAdapter(adpPro);
    }

    /**
     * Metodo que comprueba si el nombre del producto esta repetido
     * @param nombre
     * @return Devuelve un booleano para saber si esta repetido o no
     */
    public boolean comprobarDatosRepes(String nombre)
    {
        cursorRepes = manager.getItems();
        boolean resultado = false; //Inicializamos a false

        while (cursorRepes.moveToNext())
        {
            Producto item = new Producto();
            item._id = cursorRepes.getInt(cursorRepes.getColumnIndex(manager.ID_PRODUCTO));
            item._nombre = cursorRepes.getString(cursorRepes.getColumnIndex(manager.NOMBRE_PRODUCTO));
            item._cantidad = cursorRepes.getInt(cursorRepes.getColumnIndex(manager.CANTIDAD_PRODUCTO));
            item._descripcion = cursorRepes.getString(cursorRepes.getColumnIndex(manager.DESCRIPCION_PRODUCTO));
            item._precio = cursorRepes.getDouble(cursorRepes.getColumnIndex(manager.PRECIO_PRODUCTO));
            item._estado = cursorRepes.getInt(cursorRepes.getColumnIndex(manager.ESTADO_PRODUCTO));

                //Si hay un item con el nombre o email en la red seleccionada no se puede insertar o modificar
                if (item._nombre.equals(nombre))
                {
                    resultado = false;
                    break;
                }
                else {
                    resultado = true;
                }
        }
        return resultado;
    }
    //MENU CONTEXTUAL

    /**
     * Metodo que crea el menu contextual
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater menuInflater = getMenuInflater();

        if (v.getId() == this.listaPro.getId())
        {
            menuInflater.inflate(R.menu.context, menu);
        }
    }

    /**
     * Metodo que crea el actionbars
     * @param menu
     * @return devuelve un booleano
     */
    //MENU ACTION BARS
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Metodo para saber que elemento del menu contextual hemos pulsado
     * @param item
     * @return devuelve el elemento seleccionado
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {


        final AdapterView.AdapterContextMenuInfo itemPulsado = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();


        switch (item.getItemId())
        {
            case R.id.editarProducto:

                //DIALOGO EDITAR
                final Dialog dialogEdit = new Dialog(this);
                dialogEdit.setContentView(R.layout.add_layout);
                dialogEdit.setTitle(R.string.titleEditProducto);
                final EditText edtEditPro = (EditText)dialogEdit.findViewById(R.id.edtProductoName);
                final EditText edtEditCant = (EditText)dialogEdit.findViewById(R.id.edtCant);
                final EditText edtEditDesc = (EditText)dialogEdit.findViewById(R.id.edtDescrip);
                final EditText edtEditPrecio = (EditText)dialogEdit.findViewById(R.id.edtPrecio);
                Button btnEdtAceptar = (Button)dialogEdit.findViewById(R.id.btnAceptar);
                Button btnEditCancelar = (Button)dialogEdit.findViewById(R.id.btnCancelar);

                //MOSTRAMOS LOS DATOS
                edtEditPro.setText(adpPro.getItem(itemPulsado.position)._nombre);
                edtEditCant.setText(String.valueOf(adpPro.getItem(itemPulsado.position)._cantidad));
                edtEditPrecio.setText(String.valueOf(adpPro.getItem(itemPulsado.position)._precio));
                edtEditDesc.setText(adpPro.getItem(itemPulsado.position)._descripcion);

                //CLICK BOTON ACEPTAR
                btnEdtAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {

                        int id = adpPro.getItem(itemPulsado.position)._id; //Mismo ID
                        String editNombrePro = edtEditPro.getText().toString();
                        int editCantidadPro = Integer.parseInt(edtEditCant.getText().toString());
                        double editPrecioPro = Double.parseDouble(edtEditPrecio.getText().toString());
                        String editDescPro = edtEditDesc.getText().toString();

                        double precio = 0;

                        //CONTROLAMOS QUE SI EL PRECIO ES NULO SE META 0
                        if(!(edtEditPrecio.getText().toString().length() == 0))
                        {
                            precio = Double.parseDouble(edtEditPrecio.getText().toString());
                        }


                        //CONTROLAMOS QUE LA CANTIDAD NO SEA Nula
                        if (edtEditCant.getText().toString().length() == 0) {
                            showMessage(R.string.introducirCantidad);
                            return;
                        }

                        Integer cantidad = Integer.parseInt(edtEditCant.getText().toString()); //CANTIDAD DEL PRODUCTO


                        //CONTROLAMOS EL NOMBRE DEL PRODUCTO PARA QUE NO ESTE VACIO
                        if (editNombrePro.isEmpty()) {
                            showMessage(R.string.introducirNombrePro);
                            return;
                        }

                        //SI HAY DESCRIPCION QUE LA GUARDE
                        if (editDescPro.isEmpty()) {
                            editDescPro = null;
                        }

                        //CONTROLAMOS QUE LA CANTIDAD NO SEA MENOR QUE 0
                        if (cantidad <= 0) {
                            showMessage(R.string.cantidadMayor0);
                            return;
                        }

                        //CALCULAMOS EL PRECIO X CANTIDAD
                        double precioCant = precio * cantidad;

                        if (comprobarDatosRepes(editNombrePro)) {
                            if (manager.modificar(id, editNombrePro, editCantidadPro, editDescPro, precioCant)) {
                                showMessage(R.string.okModificar);
                            } else {
                                showMessage(R.string.errorModificar);
                            }
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Nombre repetido", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        cargarDatos();
                        dialogEdit.dismiss();
                    }
                });

                //CLICK BOTON CANCELAR
                btnEditCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        dialogEdit.dismiss();
                    }
                });

                dialogEdit.show();

                break;
            case R.id.eliminarProducto:

                    try
                    {
                        if(manager.eliminar(adpPro.getItem(itemPulsado.position)._id))
                        {
                            cargarDatos();
                            showMessage(R.string.borrarProducto);
                        }
                        else
                        {
                            showMessage(R.string.errorBorrar);
                        }
                    }
                    catch (Exception e)
                    {
                        showMessage(R.string.errorBorrar);
                    }

                break;
            case R.id.descripcionProducto:

                String descripcionItem = adpPro.getItem(itemPulsado.position)._descripcion;

                if(descripcionItem.toString().isEmpty()) //Si esta vacio..
                {

                    showMessage(R.string.emptyDesc);
                }
                else
                {
                    Toast.makeText(MainActivity.this, descripcionItem, Toast.LENGTH_SHORT).show();
                }


                break;
            default:
                return super.onContextItemSelected(item);
        }

        return super.onContextItemSelected(item);
    }

    /**
     * Metodo que sirve para saber que elemento del actionbars hemos pulsado
     * @param item
     * @return devuelve el elemento seleccionado
     */
    //ELEMENTOS DEL MENU
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //BOTON BORRAR_TODO
        if(id == R.id.borrarTodo)
        {
            if(manager.eliminarTodo())
            {
                cargarDatos();
                showMessage(R.string.borradoAllok);
            }
            else
            {
                showMessage(R.string.borradoAllFail);
            }

        }

        //BOTON CALCULAR PRECIO TOTAL
        if (id == R.id.calcularTotal)
        {
            if (lista.isEmpty())
            {
                showMessage(R.string.totalVacio);
            }
            else
            {
                for (int i = 0; i < arrayCantidades.size(); i++)
                {
                    cantTotal += arrayCantidades.get(i);
                }
                Toast.makeText(MainActivity.this, "TOTAL: "+ cantTotal + " Euros", Toast.LENGTH_SHORT).show();
                cantTotal = 0.0;
            }


        }

        //BOTON AÑADIR
        if (id == R.id.menuAdd)
        {
            Log.v("BOTON ADD", "OK");
            final Dialog dialogAdd = new Dialog(this);
            dialogAdd.setContentView(R.layout.add_layout);
            dialogAdd.setTitle(R.string.tittleAddProducto);
            final EditText edtProducto = (EditText)dialogAdd.findViewById(R.id.edtProductoName);
            final EditText edtDescripcion = (EditText)dialogAdd.findViewById(R.id.edtDescrip);
            final EditText edtCantidad = (EditText)dialogAdd.findViewById(R.id.edtCant);
            final EditText edtPrecio = (EditText)dialogAdd.findViewById(R.id.edtPrecio);
            Button btnAdd = (Button)dialogAdd.findViewById(R.id.btnAceptar);
            Button btnCancelar = (Button)dialogAdd.findViewById(R.id.btnCancelar);


            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    String textProducto = edtProducto.getText().toString(); //NOMBRE DEL PRODUCTO
                    String textDescripcion = edtDescripcion.getText().toString(); //DESCRIPCION DEL PRODUCTO
                    double precio = 0;

                    //CONTROLAMOS QUE SI EL PRECIO ES NULO SE META 0
                    if(!(edtPrecio.getText().toString().length() == 0))
                    {
                        precio = Double.parseDouble(edtPrecio.getText().toString());
                    }


                    //CONTROLAMOS QUE LA CANTIDAD NO SEA Nula
                    if (edtCantidad.getText().toString().length() == 0) {
                        showMessage(R.string.introducirCantidad);
                        return;
                    }

                    Integer cantidad = Integer.parseInt(edtCantidad.getText().toString()); //CANTIDAD DEL PRODUCTO


                    //CONTROLAMOS EL NOMBRE DEL PRODUCTO PARA QUE NO ESTE VACIO
                    if (textProducto.isEmpty()) {
                        showMessage(R.string.introducirNombrePro);
                        return;
                    }

                    //SI HAY DESCRIPCION QUE LA GUARDE
                    if (textDescripcion.isEmpty()) {
                        textDescripcion = null;
                    }

                    //CONTROLAMOS QUE LA CANTIDAD NO SEA MENOR QUE 0
                    if (cantidad <= 0) {
                        showMessage(R.string.cantidadMayor0);
                        return;
                    }

                    //CALCULAMOS EL PRECIO X CANTIDAD
                    double precioCant = precio * cantidad;


                    if (comprobarDatosRepes(textProducto))
                    {
                        //AGREGAMOS
                        if (manager.insertar(textProducto, cantidad, textDescripcion, precioCant) > -1)
                        {
                            //showMessage(R.string.insercionCorrecta);
                        }
                        else
                        {
                            showMessage(R.string.errorInsertar);
                        }
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Nombre Repetido", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    cargarDatos();
                    dialogAdd.dismiss();
                }
            });

            btnCancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogAdd.dismiss();
                }
            });

            dialogAdd.show();
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * Metodo que sirve para mostrar Toast por pantalla
     * @param message
     */
    private void showMessage(int message)
    {
        Context context = getApplicationContext();
        CharSequence text = getResources().getString(message);
        int duration = Toast.LENGTH_SHORT;

        Toast.makeText(context,text,duration).show();
    }
}
