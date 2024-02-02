package com.example.refuseclassification.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.refuseclassification.Database.Knowledge;
import com.example.refuseclassification.R;
import com.example.refuseclassification.setTitleCenter;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends BaseActivity {

  private static final int REQUEST_IMAGE_CAPTURE = 1;
  private static final int REQUEST_IMAGE_PICK = 2;
  List<Knowledge> knowledges = new ArrayList<>();
  private ProgressDialog progressDialog;
  private Toolbar toolbar;
  private EditText editText;
  private RecyclerView recyclerView;
  private MyAdapter myAdapter;
  private Uri photoUri;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);
    toolbar = findViewById(R.id.search_toolbar);
    toolbar.setTitle("搜索");
    new setTitleCenter().setTitleCenter(toolbar);

    knowledges = LitePal.findAll(Knowledge.class);
    recyclerView = findViewById(R.id.search_recyclerView);
    myAdapter = new SearchActivity.MyAdapter();
    recyclerView.setAdapter(myAdapter);
    LinearLayoutManager manager = new LinearLayoutManager(SearchActivity.this);
    recyclerView.setLayoutManager(manager);

    //点击相机图标图片搜索
    ImageButton searchCameraButton = findViewById(R.id.search_camera_button);
    searchCameraButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showImagePickDialog();
      }
    });

    // 输入框输入搜索
    editText = findViewById(R.id.search);

    editText.addTextChangedListener(new TextWatcher() {
      // 输入文本前的状态
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (myAdapter != null) {
          recyclerView.setAdapter(myAdapter);
        }
      }

      // 输入文本时的状态
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        String str = s.toString();
        knowledges.clear();
        knowledges = LitePal.where("name like ?", "%" + str + "%").
                find(Knowledge.class);
        if (knowledges.isEmpty()) {
          // 本地没有搜索到结果，执行网络请求
          requestSearchFromNetwork(str);
        } else {
          myAdapter = new SearchActivity.MyAdapter();
          recyclerView.setAdapter(myAdapter);
        }
      }

      // 输入文本之后的状态
      @Override
      public void afterTextChanged(Editable s) {
      }
    });
  }

  private void showProgressDialog() {
    progressDialog = new ProgressDialog(this);
    progressDialog.setMessage("加载中...");
    progressDialog.setCancelable(false); // 设置为不可取消，防止用户在操作过程中取消对话框
    progressDialog.show();
  }

  private void dismissProgressDialog() {
    if (progressDialog != null && progressDialog.isShowing()) {
      progressDialog.dismiss();
    }
  }

  private void showImagePickDialog() {
    CharSequence[] options = {"拍照", "从相册选择"};
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("选择图片来源");
    builder.setItems(options, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        if (which == 0) {
          // 拍照
          takePhoto();
        } else {
          // 从相册选择
          pickFromGallery();
        }
      }
    });
    builder.show();
  }

  private void takePhoto() {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
      // 创建文件来保存图片
      File photoFile = null;
      try {
        photoFile = createImageFile();
      } catch (IOException ex) {
        // 错误处理
      }
      // 如果文件创建成功，启动相机
      if (photoFile != null) {
        photoUri = FileProvider.getUriForFile(this,
                "com.example.refuseclassification.fileprovider",
                photoFile);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
      }
    }
  }

  private void pickFromGallery() {
    Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK);
  }

  private File createImageFile() throws IOException {
    // 创建一个唯一的文件名
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    String imageFileName = "JPEG_" + timeStamp + "_";
    File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    File image = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
    );
    return image;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
      if (requestCode == REQUEST_IMAGE_CAPTURE) {
        // 处理拍照返回的图片
        processImage(photoUri);
      } else if (requestCode == REQUEST_IMAGE_PICK) {
        // 处理从图库选择的图片
        if (data != null && data.getData() != null) {
          Uri selectedImageUri = data.getData();
          processImage(selectedImageUri);
        }
      }
    }
  }

  private void processImage(Uri imageUri) {
    showProgressDialog();
    try {
      Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
      // 可以对Bitmap进行压缩等处理
      // 将Bitmap转换为Base64字符串
      String base64String = convertBitmapToBase64(bitmap);
      sendImageToServer(base64String);
      // 处理base64String
    } catch (IOException e) {
      e.printStackTrace();
      dismissProgressDialog();
    }
  }

  private void sendImageToServer(String base64Image) {
    OkHttpClient client = new OkHttpClient();
    String apiKey = "facd5f5a85d8c96d3d50a08127975813"; // 替换为你的API密钥

    // 创建请求体
    RequestBody requestBody = new FormBody.Builder()
            .add("key", apiKey)
            .add("img", base64Image)
            .build();

    // 创建请求
    Request request = new Request.Builder()
            .url("https://apis.tianapi.com/imglajifenlei/index")
            .post(requestBody)
            .build();

    // 异步执行请求
    client.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        e.printStackTrace();
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        if (response.isSuccessful()) {
          String responseData = response.body().string();
          try {
            JSONObject jsonObject = new JSONObject(responseData);
            JSONArray list = jsonObject.getJSONObject("result").getJSONArray("list");
            knowledges.clear();
            for (int i = 0; i < list.length(); i++) {
              JSONObject item = list.getJSONObject(i);
              String name = item.getString("name");
              String kind = getTypeDescription(item.getInt("lajitype"));
              String tips = "提示: " + item.getString("lajitip");
              knowledges.add(new Knowledge(name, kind, tips));
            }
            if (knowledges.size() == 0) {
              Toast.makeText(SearchActivity.this, "未找到相关知识", Toast.LENGTH_SHORT).show();
            }
            dismissProgressDialog();
            // 在主线程中更新UI
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                // 更新适配器数据并刷新RecyclerView
                myAdapter = new SearchActivity.MyAdapter();
                recyclerView.setAdapter(myAdapter);
              }
            });
          } catch (JSONException e) {
            dismissProgressDialog();
            e.printStackTrace();
          }
        }
      }
    });
  }

  private String convertBitmapToBase64(Bitmap bitmap) {
    // 初始化压缩质量为100，代表不压缩
    int quality = 50;
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    // 尝试压缩图片
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
    // 计算压缩后的大小
    int streamLength = outputStream.size();
    // 如果压缩后的大小超过3MB，则增加压缩率
    while (streamLength > 3 * 1024 * 1024 && quality > 0) {
      // 清空outputStream以便重新压缩
      outputStream.reset();
      // 减少质量
      quality -= 5;
      // 再次压缩
      bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
      streamLength = outputStream.size();
    }
    // 将压缩后的图片转换为Base64字符串
    byte[] byteArray = outputStream.toByteArray();
    return Base64.encodeToString(byteArray, Base64.DEFAULT);
  }

  public void requestSearchFromNetwork(String str) {
    showProgressDialog();
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
            .url("https://apis.tianapi.com/lajifenlei/index?key=facd5f5a85d8c96d3d50a08127975813&word=" + str)
            .build();

    client.newCall(request).enqueue(new Callback() {
      @Override
      public void onResponse(Call call, Response response) throws IOException {
        if (response.isSuccessful()) {
          String responseBody = response.body().string();
          try {
            JSONObject jsonObject = new JSONObject(responseBody);
            JSONArray list = jsonObject.getJSONObject("result").getJSONArray("list");
            knowledges.clear();
            for (int i = 0; i < list.length(); i++) {
              JSONObject item = list.getJSONObject(i);
              String name = item.getString("name");
              String kind = getTypeDescription(item.getInt("type"));
              String tips = "提示: " + item.getString("tip");
              knowledges.add(new Knowledge(name, kind, tips));
            }
            dismissProgressDialog();
            // 在主线程中更新UI
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                // 更新适配器数据并刷新RecyclerView
                myAdapter = new SearchActivity.MyAdapter();
                recyclerView.setAdapter(myAdapter);
              }
            });
          } catch (JSONException e) {
            e.printStackTrace();
            dismissProgressDialog();
          }
        }
      }

      @Override
      public void onFailure(Call call, IOException e) {
        e.printStackTrace();
      }
    });
  }

  // 根据type值获取对应的类型描述
  private String getTypeDescription(int type) {
    switch (type) {
      case 0:
        return "可回收物";
      case 1:
        return "有害垃圾";
      case 2:
        return "湿垃圾";
      case 3:
        return "干垃圾";
      default:
        return "其他";
    }
  }

  private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = View.inflate(SearchActivity.this, R.layout.item_recyclerview, null);
      MyViewHolder myViewHolder = new MyViewHolder(view);
      return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
      Knowledge knowledge = knowledges.get(position);
      holder.name.setText(knowledge.getName());
      holder.kind.setText((knowledge.getKind()));
      holder.tips.setText((knowledge.getTips()));
    }

    @Override
    public int getItemCount() {
      return knowledges.size();
    }
  }

  private class MyViewHolder extends RecyclerView.ViewHolder {

    TextView name;
    TextView kind;
    TextView tips;

    public MyViewHolder(@NonNull View itemView) {
      super(itemView);
      name = itemView.findViewById(R.id.name);
      kind = itemView.findViewById(R.id.kind);
      tips = itemView.findViewById(R.id.tips);
    }
  }
}

