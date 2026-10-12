package com.example.vangustapp;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VolleyMultipartRequest extends Request<NetworkResponse> {
    private final Response.Listener<NetworkResponse> mListener;
    private final Response.ErrorListener mErrorListener;
    private Map<String, String> mParams;
    private Map<String, DataPart> mByteArrayParams;

    public VolleyMultipartRequest(int method, String url,
                                  Response.Listener<NetworkResponse> listener,
                                  Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
        this.mParams = new HashMap<>();
        this.mByteArrayParams = new HashMap<>();
    }

    public void setParams(Map<String, String> params) {
        this.mParams = params;
    }

    public void setByteData(Map<String, DataPart> byteData) {
        this.mByteArrayParams = byteData;
    }

    @Override
    public Map<String, String> getParams() throws AuthFailureError {
        return mParams;
    }

    // Método necessário para suportar dados binários (imagens)
    protected Map<String, DataPart> getByteData() throws AuthFailureError {
        return null;
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data; boundary=apiclient-" + System.currentTimeMillis();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        String boundary = "apiclient-" + System.currentTimeMillis();
        try {
            // Adiciona parâmetros de texto
            if (mParams != null && !mParams.isEmpty()) {
                for (Map.Entry<String, String> entry : mParams.entrySet()) {
                    bos.write(("--" + boundary + "\r\n").getBytes());
                    bos.write(("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n").getBytes());
                    bos.write((entry.getValue() + "\r\n").getBytes());
                }
            }

            // Adiciona dados binários (imagens obtidas do getByteData sobrescrito)
            Map<String, DataPart> byteData = getByteData();
            if (byteData != null && !byteData.isEmpty()) {
                for (Map.Entry<String, DataPart> entry : byteData.entrySet()) {
                    DataPart dataPart = entry.getValue();
                    bos.write(("--" + boundary + "\r\n").getBytes());
                    bos.write(("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"; filename=\"" + dataPart.getFileName() + "\"\r\n").getBytes());
                    bos.write(("Content-Type: " + dataPart.getType() + "\r\n\r\n").getBytes());
                    bos.write(dataPart.getContent());
                    bos.write(("\r\n").getBytes());
                }
            }
            bos.write(("--" + boundary + "--\r\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        mErrorListener.onErrorResponse(error);
    }

    public static class DataPart {
        private String fileName;
        private byte[] content;
        private String type;

        public DataPart(String name, byte[] data) {
            fileName = name;
            content = data;
            type = "image/jpeg";
        }

        public String getFileName() { return fileName; }
        public byte[] getContent() { return content; }
        public String getType() { return type; }
    }
}