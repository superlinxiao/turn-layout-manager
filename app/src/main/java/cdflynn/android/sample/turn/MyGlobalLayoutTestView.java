package cdflynn.android.sample.turn;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * @author lizheng
 * create at 2019/1/10
 * description:
 */
public class MyGlobalLayoutTestView extends View {
  public MyGlobalLayoutTestView(Context context) {
    super(context);
  }

  public MyGlobalLayoutTestView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public MyGlobalLayoutTestView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawColor(Color.RED);
    Log.i("onDrawTest", "on draw ");
  }
}
