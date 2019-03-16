package ro.infotop.journeytoself.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.List;

import ro.infotop.journeytoself.R;
import ro.infotop.journeytoself.commons.Disease;
import ro.infotop.journeytoself.model.userModel.Patient;
import ro.infotop.journeytoself.model.userModel.Therapist;

public class TherapistAdapter extends RecyclerView.Adapter<TherapistAdapter.ItemHolder> {
    private Context context;
    private List<Therapist> therapists;

    public TherapistAdapter(Context context, @NotNull List<Therapist> therapists) {
        this.context = context;
        this.therapists = therapists;
    }

    public void addTherapists(List<Therapist> therapists) {
        this.therapists.addAll(therapists);
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_therapist_profile, viewGroup, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder itemHolder, int i) {
        Therapist t = therapists.get(i);

        // TODO: load profile image
        itemHolder.nameTextView.setText(t.getName());
        itemHolder.locationTextView.setText(String.format("%s, %s", t.getCity(), t.getCountry()));

        StringBuilder sb = new StringBuilder();
        for (Disease illness : t.getSpecializations()) {
            sb.append(illness);
            sb.append(", ");
        }

        if (sb.length() > 2) sb.delete(sb.length() - 2, sb.length());
        itemHolder.enumTextView.setText(sb.toString());

        itemHolder.descriptionTextView.setText(t.getDescription());
    }

    @Override
    public int getItemCount() {
        return therapists.size();
    }

    public void clear() {
        therapists.clear();
    }

    public Therapist get(int position) {
        return therapists.get(position);
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView nameTextView;
        TextView locationTextView;
        private TextView enumLabelTextView;
        TextView enumTextView;
        TextView descriptionTextView;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name_textview1);
            locationTextView = itemView.findViewById(R.id.location_textview);
            enumLabelTextView = itemView.findViewById(R.id.enumeration_label_textview1);
            enumTextView = itemView.findViewById(R.id.enumeration_textview1);
            descriptionTextView = itemView.findViewById(R.id.description_textview1);
            enumLabelTextView.setText(R.string.label_specializations);
            profileImage = itemView.findViewById(R.id.profile_image);
        }
    }
}
