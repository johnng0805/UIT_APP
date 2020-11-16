package com.example.uit_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

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

    public CartFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_cart, container, false);

        totalPrice = rootView.findViewById(R.id.price_text);
        cartItemView = rootView.findViewById(R.id.cart_item_view);
        payBtn = rootView.findViewById(R.id.cart_pay_button);

        return rootView;
    }
}
