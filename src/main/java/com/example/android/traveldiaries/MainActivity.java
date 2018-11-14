package com.example.android.traveldiaries;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    static RecyclerView.Adapter mAdapter;

    static ArrayList<String> myDataset;
    static ArrayList<LatLng> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDataset = new ArrayList<>();
        locations = new ArrayList<>();

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.android.traveldiaries", Context.MODE_PRIVATE);
        ArrayList<String> latitudes = new ArrayList<>();
        ArrayList<String> longitudes = new ArrayList<>();
        try {
            myDataset = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("places", ObjectSerializer.serialize(new ArrayList<String>())));
            latitudes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("latitudes", ObjectSerializer.serialize(new ArrayList<String>())));
            longitudes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("longitudes", ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        if(myDataset.size()>0 && latitudes.size()==myDataset.size() && longitudes.size()==myDataset.size()){
            locations.clear();
            for(int i=0;i<latitudes.size();i++){
                locations.add(new LatLng(Double.parseDouble(latitudes.get(i)),Double.parseDouble(latitudes.get(i))));
            }
        }
        else {
            Log.i("size of myDataSet", myDataset.size()+"");
            Log.i("size of locations", ""+ locations.size());
            locations.add(new LatLng(0,0));
            myDataset.add("add a new place");
        }


        /*myDataset.add("Victoria Memorial");
        myDataset.add("Qutub Minar");
        myDataset.add("Sanchi Stupa");
        myDataset.add("Elephanta Caves");
        myDataset.add("Naya Raipur");
        myDataset.add("Taj Hotel");
        myDataset.add("India Gate");
        myDataset.add("Marine Drive");
        myDataset.add("Gateway of India");
        */

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(myDataset, this);
        mRecyclerView.setAdapter(mAdapter);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Activity not granted", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }
}

class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
    ArrayList<String> mDataSet;
    Context ctx;
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_text_view, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        myViewHolder.mTestView.setText(mDataSet.get(i));
        myViewHolder.mTestView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ctx, mDataSet.get(i), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ctx, MapsActivity.class);
                intent.putExtra("placeholder", i);
                intent.putExtra("place", mDataSet.get(i));
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    MyAdapter(ArrayList<String> list, Context ctx){
        mDataSet = list;
        this.ctx = ctx;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView mTestView;
        //Context context;
        public MyViewHolder(@NonNull View v) {
            super(v);
            mTestView = (TextView) v.findViewById(R.id.tvHolder);
            //context = v.getContext();


        }
        /*
        @Override
        public void onClick(View view) {
            Intent intent = new Intent()
        }
        */
    }
}
