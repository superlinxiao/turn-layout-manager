package cdflynn.android.sample.turn;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import cdflynn.android.library.turn.TurnLayoutManager;

public class MainActivity extends AppCompatActivity {

  /**
   * 屏幕中心线的x坐标
   */
  private int center;

  static class Views {
    ViewGroup root;
    RecyclerView list;
    SeekBar radius;
    TextView radiusText;
    SeekBar peek;
    TextView peekText;
    Spinner gravity;
    Spinner orientation;
    CheckBox rotate;
    View controlsHandle;
    View controls;

    Views(MainActivity activity) {
      root = (ViewGroup) activity.findViewById(R.id.root);
      list = (RecyclerView) activity.findViewById(R.id.recycler_view);
      radius = (SeekBar) activity.findViewById(R.id.seek_radius);
      radiusText = (TextView) activity.findViewById(R.id.radius_text);
      peek = (SeekBar) activity.findViewById(R.id.seek_peek);
      peekText = (TextView) activity.findViewById(R.id.peek_text);
      gravity = (Spinner) activity.findViewById(R.id.gravity);
      orientation = (Spinner) activity.findViewById(R.id.orientation);
      rotate = (CheckBox) activity.findViewById(R.id.rotate);
      controlsHandle = activity.findViewById(R.id.control_handle);
      controls = activity.findViewById(R.id.control_panel);
    }
  }

  private Views views;
  private TurnLayoutManager layoutManager;
  private SampleAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStatus();
    initView();
    center = DeviceUtil.getRealScreenWidth(MainActivity.this) / 2;
  }

  private void initView() {
    setContentView(R.layout.activity_main);
    views = new Views(this);
    adapter = new SampleAdapter(this);
    final int radius = (int) getResources().getDimension(R.dimen.list_radius);
    final int peek = (int) getResources().getDimension(R.dimen.list_peek);
    layoutManager = new TurnLayoutManager(this,
        TurnLayoutManager.Gravity.START,
        TurnLayoutManager.Orientation.HORIZONTAL,
        radius,
        peek,
        views.rotate.isChecked());
    views.list.setLayoutManager(layoutManager);
    views.list.setAdapter(adapter);
    adapter.notifyDataSetChanged();
    adapter.setOnItemClickListener(new SampleAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(View view, int position) {
        int viewCenter = (view.getLeft() + view.getRight()) / 2;
        views.list.smoothScrollBy(viewCenter - center, 0);
      }
    });
    views.radius.setOnSeekBarChangeListener(radiusListener);
    views.peek.setOnSeekBarChangeListener(peekListener);
    views.radius.setProgress(radius);
    views.peek.setProgress(peek);
    views.gravity.setOnItemSelectedListener(gravityOptionsClickListener);
    views.orientation.setOnItemSelectedListener(orientationOptionsClickListener);
    views.gravity.setAdapter(new GravityAdapter(this, R.layout.spinner_item));
    views.orientation.setAdapter(new OrientationAdapter(this, R.layout.spinner_item));
    views.rotate.setOnCheckedChangeListener(rotateListener);
    views.controlsHandle.setOnClickListener(controlsHandleClickListener);
    views.list.addOnScrollListener(new RecyclerView.OnScrollListener() {


      @Override
      public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

        super.onScrollStateChanged(recyclerView, newState);

        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
          //遍历所有的子View 计算出与中心线距离最近的View，并获取之间的距离，通过scrollBy方法移动过去，也可以通过Scroller添加动画效果
          float lastDistance = center;
          float nearestDistance = 0;
          float viewCenter;
          float viewDistance;
          View childAt;
          for (int i = 0; i < views.list.getChildCount(); i++) {
            childAt = views.list.getChildAt(i);
            if (childAt.getVisibility() == View.INVISIBLE) {
              continue;
            }
            viewCenter = (childAt.getRight() + childAt.getLeft()) / 2;
            viewDistance = Math.abs(viewCenter - center);
            if (viewDistance < lastDistance) {
              lastDistance = viewDistance;
              nearestDistance = (viewCenter - center);
            }
          }
          views.list.smoothScrollBy((int) nearestDistance, 0);
        }

      }

      @Override
      public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        //在滑动过程中，获取所有View与中心线的距离，距离越近，Scale越趋近于1，越远scale越趋近于0.5f
        float viewCenter;
        float distance;
        float scale;
        View childAt;
        for (int i = 0; i < views.list.getChildCount(); i++) {
          childAt = views.list.getChildAt(i);
          viewCenter = (childAt.getRight() + childAt.getLeft()) / 2;
          distance = Math.abs(viewCenter - center);
          scale = 0.5f * (1 + (center - distance) / center);
          childAt.setScaleX(scale);
          childAt.setScaleY(scale);
        }
      }
    });
  }

  private final SeekBar.OnSeekBarChangeListener radiusListener = new SeekBar.OnSeekBarChangeListener() {
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
      views.radiusText.setText(getResources().getString(R.string.radius_format, progress));
      if (fromUser) {
        layoutManager.setRadius(progress);
      }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
      // do nothing
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
      // do nothing
    }
  };

  private final SeekBar.OnSeekBarChangeListener peekListener = new SeekBar.OnSeekBarChangeListener() {
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
      views.peekText.setText(getResources().getString(R.string.peek_format, progress));
      if (fromUser) {
        layoutManager.setPeekDistance(progress);
      }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
      // do nothing
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
      // do nothing
    }
  };

  private final AdapterView.OnItemSelectedListener orientationOptionsClickListener = new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
      switch (position) {
        case 0:
          layoutManager.setOrientation(TurnLayoutManager.Orientation.VERTICAL);
          return;
        case 1:
          layoutManager.setOrientation(TurnLayoutManager.Orientation.HORIZONTAL);
        default:
          // do nothing
      }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
  };

  private final AdapterView.OnItemSelectedListener gravityOptionsClickListener = new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
      switch (position) {
        case 0:
          layoutManager.setGravity(TurnLayoutManager.Gravity.START);
          return;
        case 1:
          layoutManager.setGravity(TurnLayoutManager.Gravity.END);
        default:
          // do nothing
      }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
  };

  private final CompoundButton.OnCheckedChangeListener rotateListener = new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
      layoutManager.setRotate(isChecked);
    }
  };

  private final View.OnClickListener controlsHandleClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      final float translationY = views.controls.getTranslationY() == 0 ? views.controls.getHeight() : 0;
      views.controls.animate().translationY(translationY).start();
      views.controlsHandle.animate().translationY(translationY).start();
    }
  };

  private class OrientationAdapter extends ArrayAdapter<String> {
    public OrientationAdapter(@NonNull Context context, @LayoutRes int resource) {
      super(context, resource, new String[] {"Vertical", "Horizontal"});
    }
  }

  private class GravityAdapter extends ArrayAdapter<String> {
    public GravityAdapter(@NonNull Context context, @LayoutRes int resource) {
      super(context, resource, new String[] {"Start", "End"});
    }
  }

  /**
   * 设置虚拟按键和状态栏
   */
  private void setStatus() {
    int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        | View.SYSTEM_UI_FLAG_FULLSCREEN

        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    getWindow().getDecorView().setSystemUiVisibility(flags);

  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    if (hasFocus && Build.VERSION.SDK_INT >= 19) {
      setStatus();
    }
  }
}
