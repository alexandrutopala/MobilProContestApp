package ro.infotop.journeytoself.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ro.infotop.journeytoself.R;
import ro.infotop.journeytoself.commons.Disease;
import ro.infotop.journeytoself.model.therapyModel.TherapyRequest;
import ro.infotop.journeytoself.model.userModel.Patient;
import ro.infotop.journeytoself.utils.DateUtils;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientHolder> {

    private Map<Patient, TherapyRequest> patientsRequests;
    private List<Patient> patients;
    private Context context;


    public PatientAdapter(Map<Patient, TherapyRequest> patientsRequests, Context context) {
        this.patientsRequests = patientsRequests;
        this.context = context;
        patients = new ArrayList<>(patientsRequests.keySet());
    }


    @NonNull
    @Override
    public PatientHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layout = LayoutInflater.from(context).inflate(R.layout.item_patient_profile, viewGroup, false);
        return new PatientHolder(layout, i, this);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientHolder holder, int i) {
        Patient patient = patients.get(i);
        TherapyRequest request = patientsRequests.get(patient);

        // TODO: set profile image

        holder.nameTextView.setText(patient.getName());
        holder.locationTextView.setText(String.format("%s, %s", patient.getCity(), patient.getCountry()));

        StringBuilder sb = new StringBuilder();
        for (Disease illness : patient.getDiseases()) {
            sb.append(illness);
            sb.append(", ");
        }

        if (sb.length() > 2) sb.delete(sb.length() - 2, sb.length());
        holder.enumTextView.setText(sb.toString());

        if (request != null) {
            holder.emittedDate.setText(String.format("%s %s", context.getString(R.string.label_emitted_date), DateUtils.parseDateAndTime(request.getTimeStamp())));

            switch (request.getStatus()) {
                case ACCEPTED:
                    holder.acceptDeclineButtonLayout.setVisibility(View.GONE);
                    holder.cancelTherapyButton.setVisibility(View.VISIBLE);
                    break;
                case WAITING:
                    holder.acceptDeclineButtonLayout.setVisibility(View.VISIBLE);
                    holder.cancelTherapyButton.setVisibility(View.GONE);
                    break;
                case EXPIRED:
                    holder.acceptDeclineButtonLayout.setVisibility(View.GONE);
                    holder.cancelTherapyButton.setVisibility(View.GONE);
                    holder.emittedDate.setText(String.format("%s (%s)", holder.emittedDate, context.getString(R.string.suffix_expired)));
                    break;
                case REJECTED:
                    holder.cancelTherapyButton.setVisibility(View.GONE);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return patientsRequests.size();
    }

    public void clear() {
        patientsRequests.clear();
        patients.clear();
    }

    public void remove(Patient p) {
        patientsRequests.remove(p);
        patients.remove(p);
    }

    public void addAll(Map<Patient, TherapyRequest> patientTherapyRequestMap) {
        patientsRequests.putAll(patientTherapyRequestMap);
        patients.addAll(patientTherapyRequestMap.keySet());
    }

    public Patient getPatient(int i) {
        return patients.get(i);
    }

    public TherapyRequest getRequest(Patient p) {
        return patientsRequests.get(p);
    }

    public static class PatientHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView nameTextView;
        TextView locationTextView;
        private TextView enumLabelTextView;
        TextView enumTextView;
        TextView descriptionTextView;

        TextView emittedDate;
        Button viewProfileButton;
        LinearLayout acceptDeclineButtonLayout;
        Button cancelTherapyButton;
        Button acceptButton;
        Button declineButton;

        int index;
        PatientAdapter adapter;

        public PatientHolder(@NonNull View layout, int index, PatientAdapter adapter) {
            super(layout);
            this.index = index;
            this.adapter = adapter;

            View itemView = layout.findViewById(R.id.patient_profile_layout);
            nameTextView = itemView.findViewById(R.id.name_textview1);
            locationTextView = itemView.findViewById(R.id.location_textview);
            enumLabelTextView = itemView.findViewById(R.id.enumeration_label_textview1);
            enumTextView = itemView.findViewById(R.id.enumeration_textview1);
            descriptionTextView = itemView.findViewById(R.id.description_textview1);
            enumLabelTextView.setText(R.string.label_specializations);
            profileImage = itemView.findViewById(R.id.profile_image);

            emittedDate = layout.findViewById(R.id.emitted_date_textview);
            viewProfileButton = layout.findViewById(R.id.view_profile_button);
            acceptDeclineButtonLayout = layout.findViewById(R.id.accept_decline_buttons_layout);
            cancelTherapyButton = layout.findViewById(R.id.cancel_button1);
            acceptButton = layout.findViewById(R.id.accept_button);
            declineButton = layout.findViewById(R.id.decline_button);

            itemView.findViewById(R.id.description_layout).setVisibility(View.GONE);
            ((TextView) itemView.findViewById(R.id.enumeration_label_textview1)).setText(R.string.label_illnesses);

        }

    }
}
