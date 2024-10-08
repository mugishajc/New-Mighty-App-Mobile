package africa.delasoft.mighty;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import africa.delasoft.mighty.data.model.PhoneNumber;

public class CourseRVAdapter extends ListAdapter<PhoneNumber, CourseRVAdapter.ViewHolder> {

    // creating a variable for on item click listener.
    private OnItemClickListener listener;

    // creating a constructor class for our adapter class.
    CourseRVAdapter() {
        super(DIFF_CALLBACK);
    }

    // creating a call back for item of recycler view.
    private static final DiffUtil.ItemCallback<PhoneNumber> DIFF_CALLBACK = new DiffUtil.ItemCallback<PhoneNumber>() {
        @Override
        public boolean areItemsTheSame(PhoneNumber oldItem, PhoneNumber newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(PhoneNumber oldItem, PhoneNumber newItem) {
            return oldItem.getPhoneNumber().equals(newItem.getPhoneNumber());

        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // below line is use to inflate our layout
        // file for each item of our recycler view.
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.course_rv_item, parent, false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // below line of code is use to set data to
        // each item of our recycler view.
        PhoneNumber model = getCourseAt(position);


        String phoneNumber = model.getPhoneNumber();
        String courseId = "#Inc " + model.getId();

        if (phoneNumber != null && phoneNumber.length() >= 6) {
            String firstSixDigits = phoneNumber.substring(0, 6);

            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(courseId).append(" : ").append(firstSixDigits);

            // Apply styles to firstSixDigits
            StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.BLACK);
            builder.setSpan(boldSpan, courseId.length() + 3, builder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            builder.setSpan(new AbsoluteSizeSpan(16, true), courseId.length() + 3, builder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

            holder.courseNameTV.setText(builder);
        } else {
            holder.courseNameTV.setText(courseId);
        }



    }

    // creating a method to get course modal for a specific position.
    public PhoneNumber getCourseAt(int position) {
        return getItem(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // view holder class to create a variable for each view.
        TextView courseNameTV;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing each view of our recycler view.
            courseNameTV = itemView.findViewById(R.id.idTVCourseName);

            // adding on click listener for each item of recycler view.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // inside on click listener we are passing
                    // position to our item of recycler view.
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getItem(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(PhoneNumber model);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}

