package alvaro.com.listacompra;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Alvaro on 15/11/2015.
 */
public class AdapterProducto extends ArrayAdapter<Producto>
{
    private LayoutInflater lInflater;
    private ViewHolder contenedor;
    private Context cont;
    private DataBaseManager manager;

    /**
     * Constructor
     * @param context
     * @param resource
     * @param objects
     * @param lInflater
     */
    public AdapterProducto(Context context, int resource,List<Producto> objects, LayoutInflater lInflater)
    {
        super(context, resource, objects);
        cont = context;
        this.lInflater = lInflater;
        this.manager = new DataBaseManager(context);
    }

    /**
     * Metodo que devuelve una vista
     * @param position
     * @param convertView
     * @param parent
     * @return devuelve una vista
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //SI LA VISTA ESTA A NULL LA INYECTAMOS
        if (convertView == null)
        {
            convertView = this.lInflater.inflate(R.layout.producto_layout, null);

            contenedor = new ViewHolder();
            contenedor.productoView = (TextView)convertView.findViewById(R.id.txvProducto);
            contenedor.cantidadView = (TextView)convertView.findViewById(R.id.txvNumProd);
            contenedor.precioView = (TextView)convertView.findViewById(R.id.txvPrecioPro);
            contenedor.estadoView = (CheckBox)convertView.findViewById(R.id.ckbEstado); //**************************

            convertView.setTag(contenedor);
        }
        else //SI LA VISTA ESTA CREADA SE LA PASAMOS AL CONTENEDOR
        {
            contenedor = (ViewHolder)convertView.getTag();
        }

        //MOSTRAMOS LOS DATOS
        final Producto producto = getItem(position);
        contenedor.productoView.setText(producto._nombre);
        contenedor.cantidadView.setText(String.valueOf(producto._cantidad));
        contenedor.precioView.setText(String.valueOf(producto._precio));

        //CONTROLAMOS SI LA BD DEVUELVE 0 O 1
        if(producto._estado == 0)
        {
            contenedor.estadoView.setChecked(false);
        }
        else if (producto._estado == 1)
        {
            contenedor.estadoView.setChecked(true);
        }


        //CONTROLAMOS AL PULSAR
        contenedor.estadoView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    manager.modificarEstado(producto._id, 1);
                }
                else
                {
                    manager.modificarEstado(producto._id, 0);
                }
            }
        });


        convertView.setLongClickable(true);

        return convertView;
    }

    /**
     * Clase que contiene los controles donde iran los datos
     */
    private static class ViewHolder
    {
        TextView productoView;
        TextView cantidadView;
        TextView precioView;
        CheckBox estadoView;
    }
}
