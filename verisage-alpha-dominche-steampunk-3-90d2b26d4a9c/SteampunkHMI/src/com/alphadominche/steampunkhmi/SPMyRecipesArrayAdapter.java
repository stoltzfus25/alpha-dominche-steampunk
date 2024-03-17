package com.alphadominche.steampunkhmi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SPMyRecipesArrayAdapter extends ArrayAdapter<SPRecipe>{
	private final Context mContext;
	private SPRecipe[] mRecipes;
	private SPRecipe[] mFavorites;
	
	public SPMyRecipesArrayAdapter(Context context, SPRecipe[] recipes, SPRecipe[] favorites){
		super(context,R.drawable.library_recipe_name_view,recipes);
		mContext = context;
		mRecipes = recipes;
		mFavorites = favorites;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.drawable.my_recipes_list_view, parent,false);
		ImageView type = (ImageView) rowView.findViewById(R.id.recipe_type_image);
		ImageView fav = (ImageView) rowView.findViewById(R.id.favorite_star_image);
		TextView name = (TextView) rowView.findViewById(R.id.name_label);
		
		SPRecipe recipe = mRecipes[position];
		name.setText(recipe.getName());
		type.setImageResource(recipe.getImage());
		
		fav.setVisibility(8);
		for (int i = 0, len = mFavorites.length; i < len; i++) {
			if (mFavorites[i] == recipe) {
				fav.setVisibility(0);
			} 
		}
		
		return rowView;
	}
}
