package com.example.uit_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import static android.app.Activity.RESULT_OK;

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

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartItemAdapter.getItemCount() != 0) {
                    Intent intent = new Intent(getContext(), PayActivity.class);
                    startActivityForResult(intent, 1111);
                } else {
                    Toast.makeText(getContext(), "There are no courses to pay!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1111) {
            assert data != null;
            if (data.getBooleanExtra("isPaid", false)) {
                cartItemView.setVisibility(View.GONE);
                SharedPreferences sharedPreferences;
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("cartArray");
                editor.apply();
            }
        }
    }

    private void loadCourseInCart() {
        SharedPreferences sharedPreferences;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (sharedPreferences.getBoolean("diffUser", false)) {
            return;
        }
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
