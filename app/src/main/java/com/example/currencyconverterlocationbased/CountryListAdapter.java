package com.example.currencyconverterlocationbased;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CountryListAdapter extends RecyclerView.Adapter<CountryListAdapter.ViewHolder>
        implements Filterable {

    private ArrayList<Country> items;
    private ArrayList<Country> itemsFull;
    private Context context;
    private RecyclerViewSelection selection;

    private Filter countryFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Country> filteredCountries = new ArrayList<Country>();

            if (constraint == null || constraint.length() == 0) {
                filteredCountries.addAll(itemsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Country country: itemsFull) {
                    if (country.name.toLowerCase().contains(filterPattern)) {
                        filteredCountries.add(country);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredCountries;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            items.clear();
            items.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public CountryListAdapter(Context context,
                              Country[] items,
                              CountryListAdapter.RecyclerViewSelection handler) {
        this.items = new ArrayList<Country>(Arrays.asList(items));
        this.itemsFull = new ArrayList<Country>(Arrays.asList(items));
        this.context = context;
        selection = handler;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.country_list_item, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Country country = items.get(i);

        viewHolder.textView.setText(country.name);

        try {
            InputStream ims = context.getAssets().open("imagePlaceholder.png");
            Drawable drawable = Drawable.createFromStream(ims, null);

            String imageURL = "https://www.countryflags.io/" + country.identification + "/shiny/64.png";
            Picasso.get().load(imageURL).placeholder(drawable).into(viewHolder.imageView);
        }
        catch(IOException ex) {
            return;
        }

        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selection.selected(country);
            }
        });

    }

    // View holder for recycler view.
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public ImageView imageView;
        public View view;

        public ViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.countryNameTextView);
            imageView = itemView.findViewById(R.id.countryFlagImageView);
            view = itemView;
        }
    }

    public interface RecyclerViewSelection {
        public void selected(Country country);
    }

    @Override
    public Filter getFilter() {
        return countryFilter;
    }
}
