package com.example.refuseclassification.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.refuseclassification.Database.Knowledge;
import com.example.refuseclassification.R;
import com.example.refuseclassification.setTitleCenter;

import java.util.ArrayList;
import java.util.List;

public class AnswerActivity extends BaseActivity {

  private Toolbar toolbar;
  private TextView score_view;
  private List<Knowledge> knowledges = new ArrayList<>();
  private int score;
  private RecyclerView recyclerView;
  private MyAdapter myAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_answer);
    toolbar = findViewById(R.id.test_toolbar);
    toolbar.setTitle("考试结果");
    new setTitleCenter().setTitleCenter(toolbar);

    score_view = findViewById(R.id.score);
    // 获取数据
    Intent intent = getIntent();
    Bundle bundle = intent.getBundleExtra("message");
    knowledges = (List<Knowledge>) bundle.getSerializable("knowledges");
    score = bundle.getInt("score");
    score_view.setText("得分: " + score + "/100");

    // 设置recyclerView
    recyclerView = findViewById(R.id.answer_recyclerView);
    myAdapter = new MyAdapter();
    recyclerView.setAdapter(myAdapter);
    LinearLayoutManager manager = new LinearLayoutManager(AnswerActivity.this);
    recyclerView.setLayoutManager(manager);
  }

  private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = View.inflate(AnswerActivity.this, R.layout.item_answer, null);
      MyViewHolder myViewHolder = new MyViewHolder(view);
      return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
      Knowledge knowledge = knowledges.get(position);
      holder.question_done.setText(knowledge.getName());
      holder.right_answer.setText(knowledge.getKind());
      holder.my_answer.setText(knowledge.getAnswer());
    }

    @Override
    public int getItemCount() {
      return knowledges.size();
    }
  }

  private class MyViewHolder extends RecyclerView.ViewHolder {

    TextView question_done;
    TextView my_answer;
    TextView right_answer;

    public MyViewHolder(@NonNull View itemView) {
      super(itemView);
      question_done = itemView.findViewById(R.id.question_done);
      my_answer = itemView.findViewById(R.id.my_answer);
      right_answer = itemView.findViewById(R.id.right_answer);
    }
  }
}
