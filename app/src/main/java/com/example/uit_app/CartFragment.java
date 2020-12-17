package com.example.uit_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import Model.CourseItem;
import Retrofit.IMyService;
import retrofit2.Retrofit;
import Retrofit.*;

public class CartFragment extends Fragment {

    TextView totalPrice;
    RecyclerView cartItemView;
    CartItemAdapter cartItemAdapter;
    Button payBtn;

    ArrayList<CourseItem> courseItems = new ArrayList<>();

    IMyService iMyService;
    Retrofit retrofit;

    NumberFormat numberFormat = new DecimalFormat("#,###");
    double price = 0;

    public CartFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_cart, container, false);

        totalPrice = rootView.findViewById(R.id.cart_total_price);
        cartItemView = rootView.findViewById(R.id.cart_item_view);
        payBtn = rootView.findViewById(R.id.cart_pay_button);

        cartItemAdapter = new CartItemAdapter(getContext(), courseItems);
        cartItemView.setAdapter(cartItemAdapter);
        cartItemView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));

        loadCourseInCart();

        return rootView;
    }

    private void loadCourseInCart() {
        SharedPreferences sharedPreferences;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        JSONArray cartArray;
        try {
            cartArray = new JSONArray(sharedPreferences.getString("cartArray", ""));
            if (cartArray.length() == 0) {
                payBtn.setClickable(false);
                payBtn.setFocusable(false);
            } else {
                for (int i = 0; i < cartArray.length(); i++) {
                    JSONObject jo = cartArray.getJSONObject(i);

                    CourseItem item = new CourseItem();
                    item.setTitle(jo.getString("title"));
                    item.setUrl(jo.getString("courseImage"));
                    item.setAuthor(jo.getString("author"));
                    item.setID(jo.getString("courseID"));
                    item.setPrice(Float.parseFloat(jo.getString("price")));
                    item.setDiscount(Float.parseFloat(jo.getString("discount")));

                    courseItems.add(item);
                    price += (double)item.getPrice();
                }
                if (cartItemAdapter.getItemCount() == 0) {
                    price = 0;
                    payBtn.setVisibility(View.GONE);
                }
                totalPrice.setText(numberFormat.format(price));
                cartItemAdapter.notifyDataSetChanged();
            }

        } catch (JSONException jx) {
            jx.printStackTrace();
        }
    }
}
