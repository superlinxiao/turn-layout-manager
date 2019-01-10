package cdflynn.android.sample.turn;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SampleAdapter extends RecyclerView.Adapter<SampleAdapter.SampleViewHolder> {

  private final LayoutInflater layoutInflater;
  private OnItemClickListener onItemClickListener;

  public SampleAdapter(Context context) {
    this.layoutInflater = LayoutInflater.from(context);
  }

  @Override
  public SampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    TextView sampleView = (TextView) layoutInflater.inflate(R.layout.view_sample, parent, false);
    return new SampleViewHolder(sampleView);
  }

  @Override
  public void onBindViewHolder(final SampleViewHolder holder, final int position) {
//    if (position < 3 || position > getItemCount() - 4) {
//      holder.itemView.setVisibility(View.INVISIBLE);
//    } else {
//      holder.itemView.setVisibility(View.VISIBLE);
//    }

    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (onItemClickListener != null) {
          onItemClickListener.onItemClick(holder.itemView, position);
        }
      }
    });
    holder.tv.setText(Integer.toString(position));
  }

  @Override
  public int getItemCount() {
    return 5;
  }

  class SampleViewHolder extends RecyclerView.ViewHolder {

    TextView tv;

    public SampleViewHolder(View itemView) {
      super(itemView);
      this.tv = (TextView) itemView;
    }
  }

  public interface OnItemClickListener {
    void onItemClick(View view, int position);
  }

  public void setOnItemClickListener(OnItemClickListener listener) {
    onItemClickListener = listener;
  }


}
