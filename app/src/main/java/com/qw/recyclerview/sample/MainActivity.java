package com.qw.recyclerview.sample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qw.recyclerview.sample.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding bind;
    private ArrayList<String> modules = new ArrayList<>();
    private QAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());
        adapter = new QAdapter();
        bind.mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bind.mRecyclerView.setAdapter(adapter);
        for (int i = 0; i < 20; i++) {
            modules.add("" + i);
        }
        adapter.notifyDataSetChanged();
    }

    private class QAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(MainActivity.this).inflate(android.R.layout.simple_list_item_1, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof Holder) {
                ((Holder) holder).initData(position);
            }
        }

        @Override
        public int getItemCount() {
            return modules.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            private final TextView label;

            public Holder(@NonNull View itemView) {
                super(itemView);
                label = (TextView) itemView;
            }

            public void initData(int position) {
                String text = modules.get(position);
                label.setText(text);
            }
        }
    }
}