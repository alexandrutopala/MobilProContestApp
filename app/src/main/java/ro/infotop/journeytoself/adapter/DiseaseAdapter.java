package ro.infotop.journeytoself.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.infotop.journeytoself.R;
import ro.infotop.journeytoself.RegisterActivity;
import ro.infotop.journeytoself.commons.Disease;

public class DiseaseAdapter extends RecyclerView.Adapter<DiseaseAdapter.ItemHolder> {

    private Map<Disease, Boolean> checkedDiseases = new HashMap<>();
    private List<Disease> diseases;
    private Context context;

    public DiseaseAdapter(List<Disease> diseases, Context context) {
        this.diseases = diseases;
        for (Disease s : diseases) {
            checkedDiseases.put(s, false);
        }
        this.context = context;
    }

    public void addCheckedDisease(List<Disease> diseases) {
        for (Disease d : diseases) {
            checkedDiseases.put(d, true);
        }
    }

    public void addDiseases(List<Disease> list) {
        diseases.addAll(list);
        for (Disease s : list) {
            checkedDiseases.put(s, false);
        }
    }

    public List<Disease> getCheckedDiseases() {
        List<Disease> list = new ArrayList<>();
        for (Disease disease : checkedDiseases.keySet()) {
            if (checkedDiseases.get(disease)) {
                list.add(disease);
            }
        }
        return list;
    }

    public boolean existSelectedItems() {
        for (Disease key : checkedDiseases.keySet()) {
            if (checkedDiseases.get(key)) {
                return true;
            }
        }
        return false;
    }
    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View item = LayoutInflater.from(context).inflate(R.layout.item_disease, viewGroup, false);
        return new ItemHolder(item, checkedDiseases);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder itemHolder, int i) {
        itemHolder.setText(diseases.get(i).toString());
        itemHolder.setChecked(
                checkedDiseases.get(diseases.get(i))
        );
    }

    @Override
    public int getItemCount() {
        return diseases.size();
    }

    public void clear() {
        diseases.clear();
        checkedDiseases.clear();
    }

    public static class ItemHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        private CheckBox diseaseCheckBox;
        private Map<Disease, Boolean> checkedDiseases;

        public ItemHolder(@NonNull View itemView, Map<Disease, Boolean> checkedDiseases) {
            super(itemView);
            this.checkedDiseases = checkedDiseases;
            diseaseCheckBox = itemView.findViewById(R.id.disease_check_box);
            diseaseCheckBox.setOnCheckedChangeListener(this);
        }

        public ItemHolder(@NonNull View itemView, Map<Disease, Boolean> checkedDiseases, boolean checked) {
            this(itemView, checkedDiseases);
            diseaseCheckBox.setChecked(checked); // TODO: verify if onCheck gets called
        }

        public void setText(String text) {
            diseaseCheckBox.setText(text);
        }

        public boolean isChecked() {
            return diseaseCheckBox.isChecked();
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Disease disease = Disease.findDiseaseByString(buttonView.getText().toString());
            checkedDiseases.put(disease, isChecked);
        }

        public void setChecked(Boolean aBoolean) {
            diseaseCheckBox.setChecked(aBoolean);
        }
    }
}
