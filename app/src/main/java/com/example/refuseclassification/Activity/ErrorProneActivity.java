package com.example.refuseclassification.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import com.example.refuseclassification.Database.Knowledge;
import com.example.refuseclassification.R;
import com.example.refuseclassification.setTitleCenter;
import org.litepal.LitePal;

import java.io.Serializable;
import java.util.*;

public class ErrorProneActivity extends BaseActivity {

  private final List<Knowledge> knowledges = new ArrayList<>();
  private Toolbar toolbar;
  private TextView question_num;
  private TextView question;
  private Button submit;
  private RadioGroup radiogroup;
  private RadioButton answer1;
  private RadioButton answer2;
  private RadioButton answer3;
  private RadioButton answer4;
  private String answer = "";
  private int score = 0;
  private int count;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_error_prone);
    toolbar = findViewById(R.id.test_toolbar);
    toolbar.setTitle("易错练习");

    count = 0;
    new setTitleCenter().setTitleCenter(toolbar);// 初始化ToolBar
    // 初始化随机数列表，10个1~100的数
    Set<Integer> hashSet = new HashSet<Integer>();
    while (hashSet.size() != 10) {
      int number = (int) (Math.random() * 100);
      hashSet.add(number);
    }
    // 初始化问题列表
    Iterator it = hashSet.iterator();
    while (it.hasNext()) {
      int id = Integer.parseInt(it.next().toString());
      Knowledge knowledge = LitePal.find(Knowledge.class, id);
      knowledges.add(knowledge);
    }

    // 设置题目
    question = findViewById(R.id.question);
    question_num = findViewById(R.id.question_num);
    radiogroup = findViewById(R.id.radioGroup);
    answer1 = findViewById(R.id.answer1);
    answer2 = findViewById(R.id.answer2);
    answer3 = findViewById(R.id.answer3);
    answer4 = findViewById(R.id.answer4);
    submit = findViewById(R.id.submit);
    question_num.setText(Integer.toString(count + 1));
    question.setText(knowledges.get(count).getName());

    radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup group, int checkedId) {
        // 使用映射来简化状态检查和颜色设置的逻辑
        List<RadioButton> radioButtonList = Arrays.asList(answer1, answer2, answer3, answer4);
        Map<RadioButton, String> answers = new HashMap<>();
        answers.put(answer1, "可回收物");
        answers.put(answer2, "有害垃圾");
        answers.put(answer3, "湿垃圾");
        answers.put(answer4, "干垃圾");
        Map<RadioButton, String> defaultColors = new HashMap<>();
        defaultColors.put(answer1, "#000000");
        defaultColors.put(answer2, "#000000");
        defaultColors.put(answer3, "#000000");
        defaultColors.put(answer4, "#000000");

        // 遍历单选按钮，根据按钮是否被选中更新文字颜色和答案
        for (RadioButton radioButton : radioButtonList) {
          if (radioButton.getId() == checkedId) {
            // 当前按钮被选中，更新答案和选中文本的颜色
            answer = answers.get(radioButton);
            radioButton.setTextColor(Color.parseColor("#FF0033"));
          } else {
            // 当前按钮没有被选中，恢复答案和选中文本的默认颜色
            radioButton.setTextColor(Color.parseColor(defaultColors.get(radioButton)));
          }
        }
      }

    });

    submit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        radiogroup.clearCheck();
        if (count < 10) {
          if (!answer.equals("")) {
            // 如果答案不为空，更新分数和答案
            if (answer.equals(knowledges.get(count).getKind())) {
              score += 10;
            }
            Knowledge knowledge = knowledges.get(count);
            knowledge.setAnswer(answer);
            knowledges.set(count, knowledge);
          }
          count = count + 1;
          if (count != 10) {
            question_num.setText(Integer.toString(count + 1));
            question.setText(knowledges.get(count).getName());
          } else {
            Intent intent = new Intent(ErrorProneActivity.this, AnswerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("knowledges", (Serializable) knowledges);
            bundle.putInt("score", score);
            intent.putExtra("message", bundle);
            startActivity(intent);
            finish();
          }
        }
      }
    });
  }
}
