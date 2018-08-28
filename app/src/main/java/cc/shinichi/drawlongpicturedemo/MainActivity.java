package cc.shinichi.drawlongpicturedemo;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cc.shinichi.drawlongpicturedemo.data.Info;
import cc.shinichi.drawlongpicturedemo.util.DrawLongPictureUtil;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	private int REQUEST_CODE_CHOOSE = 0x000011;
	private List<String> mCurrentSelectedPath = new ArrayList<>();
	private String resultPath;

	private DrawLongPictureUtil drawLongPictureUtil;

	private TextView tv_image_result;
	private EditText et_content;
	private ProgressBar progressBar;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tv_image_result = findViewById(R.id.tv_image_result);
		et_content = findViewById(R.id.et_content);
		progressBar = findViewById(R.id.progressBar);
		progressBar.setVisibility(View.GONE);

		findViewById(R.id.btn_pick_image).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View view) {
				Matisse.from(MainActivity.this)
					.choose(MimeType.ofImage())
					.countable(true)
					.maxSelectable(9)
					.restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
					.thumbnailScale(0.85f)
					.imageEngine(new GlideEngine())
					.forResult(REQUEST_CODE_CHOOSE);
			}
		});

		findViewById(R.id.btn_draw_longpic).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View view) {
				progressBar.setVisibility(View.VISIBLE);

				drawLongPictureUtil = new DrawLongPictureUtil(MainActivity.this);
				drawLongPictureUtil.setListener(new DrawLongPictureUtil.Listener() {
					@Override public void onSuccess(String path) {
						runOnUiThread(new Runnable() {
							@Override public void run() {
								progressBar.setVisibility(View.GONE);
								Toast.makeText(MainActivity.this.getApplicationContext(), "长图生成完成，点击下方按钮查看！",
									Toast.LENGTH_LONG).show();
							}
						});
						resultPath = path;
					}

					@Override public void onFail() {
						runOnUiThread(new Runnable() {
							@Override public void run() {
								progressBar.setVisibility(View.GONE);
							}
						});
					}
				});
				Info info = new Info();
				info.setContent(et_content.getText().toString());
				info.setImageList(mCurrentSelectedPath);
				drawLongPictureUtil.setData(info);
				drawLongPictureUtil.startDraw();
			}
		});

		findViewById(R.id.btn_preview_longpic).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View view) {
				if (TextUtils.isEmpty(resultPath)) {
					return;
				}
				Intent intent = new Intent(Intent.ACTION_VIEW);
				Uri uri;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
					uri = FileProvider.getUriForFile(MainActivity.this,
						getApplicationContext().getPackageName() + ".provider", new File(resultPath));
					intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
				} else {
					uri = Uri.fromFile(new File(resultPath));
				}
				intent.setDataAndType(uri, "image/*");
				startActivity(intent);
			}
		});
	}

	@Override protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
			mCurrentSelectedPath = Matisse.obtainPathResult(data);
			Log.d("Matisse", "mSelected: " + mCurrentSelectedPath);

			final StringBuffer stringBuffer = new StringBuffer();
			for (String path : mCurrentSelectedPath) {
				stringBuffer.append(path).append("/n");
			}

			runOnUiThread(new Runnable() {
				@Override public void run() {
					tv_image_result.setText(stringBuffer.toString());
				}
			});
		}
	}

	@Override protected void onDestroy() {
		super.onDestroy();
		if (drawLongPictureUtil != null) {
			drawLongPictureUtil.removeListener();
		}
	}
}