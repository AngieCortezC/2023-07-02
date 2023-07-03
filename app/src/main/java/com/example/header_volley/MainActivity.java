package com.example.header_volley;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import WebServices.Asynchtask;
import WebServices.WebService;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Función del botón
    public void btnConsultar (View view){
        //Objeto para petición con Volley
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://api.uealecpeterson.net/public/login";
        EditText txtCorreo=findViewById(R.id.etxt1CorreoElec);
        EditText txtClave=findViewById(R.id.etxt2Contrasena);
        TextView txtToken = findViewById(R.id.txt4Token);

        if(!txtCorreo.getText().toString().isEmpty() && !txtClave.getText().toString().isEmpty()){
            StringRequest sr = new StringRequest(Request.Method.POST, url,new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject objetoJSON=  new JSONObject(response);

                        obtenerDatos(objetoJSON.getString("access_token"));
                    } catch (JSONException e) {
                        txtToken.setText(e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    txtToken.setText(error.networkResponse.statusCode +"  Error");
                }
            }) {
                @Override
                public byte[] getBody() throws AuthFailureError {
                    HashMap<String, String> params2 = new HashMap<String, String>();
                    params2.put("correo", txtCorreo.getText().toString().trim());
                    params2.put("clave", txtClave.getText().toString().trim());
                    return new JSONObject(params2).toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };

            queue.add(sr);
        }else{
            txtToken.setText("Debe completar los campos Correo y Clave");
        }
    }

    public void obtenerDatos(String autorizacion){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://api.uealecpeterson.net/public/productos/search";
        TextView txtDatos = findViewById(R.id.txt4Token);

        if(!autorizacion.isEmpty()){
            StringRequest sr = new StringRequest(Request.Method.POST, url,new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject objetoJSON=  new JSONObject(response);
                        JSONArray listaProductos = objetoJSON.getJSONArray("productos");
                        String informacionProductos="";

                        for(int i=0; i< listaProductos.length();i++){
                            JSONObject producto= listaProductos.getJSONObject(i);

                            informacionProductos+=("["+producto.getString("id")+", "+producto.getString("descripcion")+", "+producto.getString("precio_unidad")+"]\n");
                        }

                        txtDatos.setText(informacionProductos.toString());
                    } catch (JSONException e) {
                        txtDatos.setText(e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    txtDatos.setText(error.networkResponse.statusCode +"  Error");
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("fuente", "1");
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headerMap = new HashMap<String, String>();
                    headerMap.put("Authorization", "Bearer " + autorizacion);
                    return headerMap;
                }
            };

            queue.add(sr);
        }else{
            txtDatos.setText("El token de autorización no es válido");
        }
    }



}