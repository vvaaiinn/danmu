package com.example.testdanmu;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity {

	private String[] data;
	private static RelativeLayout containerVG;
	private static Button btn;
	private MyHandler handler;
	private static Boolean isFinish;

	private ArrayList<HeiAndLow> hl; // ���ÿ��Textview�ĸߺ͵�
	private ArrayList<TextView> tvs; // ���textview
	private ArrayList<Animation> anis; // ��Ŷ���
	private int currentLine = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		containerVG = (RelativeLayout) findViewById(R.id.container);
		btn = (Button) findViewById(R.id.start);

		hl = new ArrayList<HeiAndLow>();
		tvs = new ArrayList<TextView>();
		anis = new ArrayList<Animation>();

		handler = new MyHandler(this);
		data = new String[] { "a��������������������������������������", "bb������������������������������������b",
				"cccccc�ճղ����ȲȲȲȲȲȲȲȲȲȲȲȲȲȲȲȲȲȲȲȲ�", "��������������������������������������������������",
				"e�������������������������", "f����", "g�¸¸¸¸¸¹�����",
				"hh�����������������", "iһһһһһһһһһһһһһһi",
				"j�����������������ڽ����ڽ����ڽ�����j", "end" };
		isFinish = false;
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO �Զ����ɵķ������
				if (containerVG.getChildCount() > 0) {
					return;
				}
				new Thread(new DanmuRunable()).start();
			}
		});
	}

	private class DanmuRunable implements Runnable {

		@Override
		public void run() {
			// TODO �Զ����ɵķ������
			for (int i = 0; i < data.length; i++) {
				handler.obtainMessage(1, i, 0).sendToTarget();
				SystemClock.sleep(3000);
			}
		}

	}

	private static class MyHandler extends Handler {
		private WeakReference<MainActivity> ref;

		public MyHandler(MainActivity ac) {
			// TODO �Զ����ɵĹ��캯�����
			ref = new WeakReference<>(ac);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == 1) {
				MainActivity ac = ref.get();
				if (ac != null && ac.data != null) {
					int index = msg.arg1;
					String content = ac.data[index];
					float textSize = 16;
					int textColor = Color.parseColor("#eeeeee");
					System.out.println(content);
					ac.show(content, textSize, textColor);
				}
			} else if (msg.what == 2) {
				// Log.e("isFinish", isFinish + "_-");
			}
		}
	}

	private void show(String content, float textSize, int textColor) {
		final TextView textView = new TextView(this);
		// isFinish = false;
		textView.setText(content);
		textView.setTextSize(textSize);
		textView.setTextColor(textColor);
		textView.setMaxLines(3);
		RelativeLayout.LayoutParams params = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// params.addRule(RelativeLayout.ALIGN_BOTTOM);
		// params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		// params.bottomMargin = 10;
		textView.setLayoutParams(params);
		textView.post(new Runnable() {

			@Override
			public void run() {
				// TODO �Զ����ɵķ������
				// Log.e("isFi", isFinish + "++");
				Log.e("lineCount", textView.getLineCount() + "");
				// textView.setTag(textView.getLineCount());
				// isFinish = true;

				currentLine += textView.getLineCount();
				if (currentLine > 5) {
					currentLine -= hl.get(0).lineNum;
					containerVG.removeView(tvs.get(0));
					hl.remove(0);
					tvs.remove(0);
					anis.remove(0);

				}
				HeiAndLow hal = new HeiAndLow();
				hal.ll = 0;
				hal.lineNum = textView.getLineCount();
				hal.hh = (textView.getLineCount() > 3 ? 3 : textView
						.getLineCount())
						* ScreenUtils.dp2px(getApplicationContext(), 16 * 1.5f);
				textView.setVisibility(View.VISIBLE);
				if (hl.size() == 0) {
					hl.add(hal);
					tvs.add(textView);

					Animation anim1 = AnimationHelper.createTranslateAnim(
							getApplicationContext(), containerVG.getBottom()
									- hal.ll, containerVG.getBottom() - hal.hh);
					anis.add(anim1);
					textView.startAnimation(anim1);
				} else {
					anis.clear();
					for (int i = 0; i < hl.size(); i++) {
						hl.get(i).ll = hl.get(i).hh;
						hl.get(i).hh += hal.hh;

						Animation anim = AnimationHelper.createTranslateAnim(
								getApplicationContext(),
								containerVG.getBottom() - hl.get(i).ll,
								containerVG.getBottom() <= hl.get(i).hh ? -tvs
										.get(i).getLineHeight()
										* ScreenUtils.dp2px(
												getApplicationContext(),
												textView.getTextSize() * 1.5f)
										: containerVG.getBottom()
												- hl.get(i).hh);
						tvs.get(i).startAnimation(anim);
					}

					hl.add(hal);
					tvs.add(textView);
					Animation anim2 = AnimationHelper.createTranslateAnim(
							getApplicationContext(),
							containerVG.getBottom() - 20,
							containerVG.getBottom() - hal.hh);
					textView.startAnimation(anim2);
					anis.add(anim2);
				}

				for (Animation anim : anis) {
					anim.setAnimationListener(new Animation.AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {
						}

						@Override
						public void onAnimationEnd(Animation animation) {

						}

						@Override
						public void onAnimationRepeat(Animation animation) {

						}
					});
				}
				handler.sendEmptyMessage(2);
			}
		});
		containerVG.addView(textView);
		textView.setVisibility(View.INVISIBLE);

	}

	public class HeiAndLow {

		public int hh;
		public int ll;
		public int lineNum;
	}

	public static class AnimationHelper {
		/**
		 * ����ƽ�ƶ���
		 */
		public static Animation createTranslateAnim(Context context, int fromY,
				int toY) {
			TranslateAnimation tlAnim = new TranslateAnimation(0, 0, fromY, toY);
			// �Զ�����ʱ��
			long duration = 300;
			// (long) (Math.abs(toY - fromY) * 1.0f
			// / containerVG.getHeight() * 600);
			Log.e("duration", duration + "ms");
			tlAnim.setDuration(duration);
			tlAnim.setInterpolator(new DecelerateAccelerateInterpolator());
			tlAnim.setFillAfter(true);

			return tlAnim;
		}
	}

}
