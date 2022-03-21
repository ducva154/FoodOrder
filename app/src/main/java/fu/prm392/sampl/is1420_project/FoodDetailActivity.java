package fu.prm392.sampl.is1420_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import fu.prm392.sampl.is1420_project.dao.BasketDAO;
import fu.prm392.sampl.is1420_project.dao.BasketItemDAO;
import fu.prm392.sampl.is1420_project.dao.CartDAO;
import fu.prm392.sampl.is1420_project.dao.FoodDAO;
import fu.prm392.sampl.is1420_project.dao.RestaurantDAO;
import fu.prm392.sampl.is1420_project.dao.UserDAO;
import fu.prm392.sampl.is1420_project.dto.BasketDTO;
import fu.prm392.sampl.is1420_project.dto.BasketDocument;
import fu.prm392.sampl.is1420_project.dto.BasketItemDTO;
import fu.prm392.sampl.is1420_project.dto.BasketItemDocument;
import fu.prm392.sampl.is1420_project.dto.CartDTO;
import fu.prm392.sampl.is1420_project.dto.CartDocument;
import fu.prm392.sampl.is1420_project.dto.FoodDTO;
import fu.prm392.sampl.is1420_project.dto.RestaurantDTO;
import fu.prm392.sampl.is1420_project.dto.UserDTO;

public class FoodDetailActivity extends AppCompatActivity {

    private Button btnMinus, btnPlus, btnAddToCart;
    private FloatingActionButton btnBack;
    private TextView txtFoodName, txtDescription, txtQuantity, txtPrice;
    private ImageView imgFood;
    private String restaurantID, foodID;
    private FoodDTO foodDTO;
    private RestaurantDTO restaurantDTO;
    private int quantity;
    private double price;
    private BasketItemDocument basketItemDocumentAvailable = null;
    private BasketDocument basketDocumentAvailable = null;
    private Task<DocumentReference> ref;
    private BasketItemDTO basketItemDTO, preBasketItem;
    private BasketDTO basketDTO, preBasket;
    private CartDTO cartDTO, preCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);
        btnBack = findViewById(R.id.btnBack);
        btnPlus = findViewById(R.id.btnPlus);
        btnMinus = findViewById(R.id.btnMinus);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        txtFoodName = findViewById(R.id.txtFoodName);
        txtDescription = findViewById(R.id.txtDescription);
        txtQuantity = findViewById(R.id.txtQuantity);
        txtPrice = findViewById(R.id.txtPrice);
        imgFood = findViewById(R.id.imgFood);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FoodDetailActivity.this, RestaurantMenuActivity.class);
                intent.putExtra("restaurantID", restaurantID);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        foodID = intent.getStringExtra("foodID");
        if (foodID == null) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            finish();
        }
        restaurantID = intent.getStringExtra("restaurantID");
        if (restaurantID == null) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            finish();
        }

        RestaurantDAO restaurantDAO = new RestaurantDAO();
        restaurantDAO.getRestaurantByID(restaurantID).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                restaurantDTO = documentSnapshot.get("restaurantsInfo", RestaurantDTO.class);
            }
        });


        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity += 1;
                price += Double.parseDouble(txtPrice.getText().toString());
                txtQuantity.setText(String.format("%d", quantity));
                btnAddToCart.setText(String.format("Add To Cart - %s", price));
            }
        });

        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity == 0) {
                    return;
                }
                quantity -= 1;
                price -= Double.parseDouble(txtPrice.getText().toString());
                txtQuantity.setText(String.format("%d", quantity));
                btnAddToCart.setText(String.format("Add To Cart - %s", price));
            }
        });

        txtQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (quantity == 0) {
                    btnAddToCart.setVisibility(View.GONE);
                } else {
                    btnAddToCart.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    addToCart();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        loadData();
        loadCart();

    }

    private void loadCart() {
        CartDAO cartDAO = new CartDAO();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        cartDAO.getCartByUserID(user.getUid()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                    CartDocument cartDocument = doc.toObject(CartDocument.class);
                    preCart = doc.toObject(CartDocument.class).getCartsInfo();
                    cartDTO = cartDocument.getCartsInfo();
                    BasketDAO basketDAO = new BasketDAO();
                    basketDAO.getBasketByRestaurantID(restaurantID).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                List<DocumentSnapshot> doc = queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot d : doc) {
                                    basketDocumentAvailable = d.toObject(BasketDocument.class);
                                    if (basketDocumentAvailable.getCartsInfo().getCartID().equals(cartDocument.getCartsInfo().getCartID())) {
                                        basketDTO = basketDocumentAvailable.getBasketsInfo();
                                        preBasket = d.toObject(BasketDocument.class).getBasketsInfo();
                                        BasketItemDAO basketItemDAO = new BasketItemDAO();
                                        basketItemDAO.getBasketItemByBasketID(basketDocumentAvailable.getBasketsInfo().getBasketID()).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                if (!queryDocumentSnapshots.isEmpty()) {
                                                    List<DocumentSnapshot> doc = queryDocumentSnapshots.getDocuments();
                                                    for (DocumentSnapshot d : doc) {
                                                        basketItemDocumentAvailable = d.toObject(BasketItemDocument.class);
                                                        if (basketItemDocumentAvailable.getFoodsInfo().getFoodID().equals(foodID)) {
                                                            basketItemDTO = basketItemDocumentAvailable.getBasketItemsInfo();
                                                            preBasketItem = d.toObject(BasketItemDocument.class).getBasketItemsInfo();
                                                            price = basketItemDocumentAvailable.getBasketItemsInfo().getPrice();
                                                            quantity = basketItemDocumentAvailable.getBasketItemsInfo().getQuantity();
                                                            txtQuantity.setText(String.format("%d", quantity));
                                                            btnAddToCart.setText(String.format("Add To Cart - %s", price));
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }

                            }
                        }
                    });
                }
            }
        });
    }

    private void loadData() {
        try {
            FoodDAO foodDAO = new FoodDAO();
            foodDAO.getFoodByID(foodID).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    foodDTO = documentSnapshot.get("foodsInfo", FoodDTO.class);
                    txtFoodName.setText(foodDTO.getName());
                    txtDescription.setText(foodDTO.getDescription());
                    txtPrice.setText(foodDTO.getPrice() + "");
                    price = Double.parseDouble(txtPrice.getText().toString());
                    quantity = Integer.parseInt(txtQuantity.getText().toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addToCart() {
        if (preBasket != null && preBasketItem == null) {
            //different item so update it
            basketItemDTO = new BasketItemDTO();
            basketItemDTO.setQuantity(quantity);
            basketItemDTO.setPrice(price);
            double totalPrice = 0;
            int totalQuantity = 0;
            for (BasketItemDTO list : basketDocumentAvailable.getBasketItemsInfo()) {
                totalPrice += list.getPrice();
                totalQuantity += list.getQuantity();
            }
            basketDTO.setBasketQuantity(totalQuantity + quantity);
            basketDTO.setBasketPrice(totalPrice + price);
            CartDAO cartDAO = new CartDAO();
            cartDAO.updateCartSameBasket(cartDTO, basketDTO, basketItemDTO, preBasket, foodDTO).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Intent intent = new Intent(FoodDetailActivity.this, RestaurantMenuActivity.class);
                    intent.putExtra("restaurantID", restaurantID);
                    startActivity(intent);
                }
            });
        } else if (preBasketItem != null) {
            System.out.println("vao 2");
            //same item so update it
            basketItemDTO.setQuantity(quantity);
            basketItemDTO.setPrice(price);
            double totalPrice = 0;
            int totalQuantity = 0;
            for (BasketItemDTO list : basketDocumentAvailable.getBasketItemsInfo()) {
                totalPrice += list.getPrice();
                totalQuantity += list.getQuantity();
            }
            if (quantity >= preBasketItem.getQuantity()) {
                totalPrice = totalPrice + (price - preBasketItem.getPrice());
                totalQuantity = totalQuantity + (quantity - preBasketItem.getQuantity());
            } else {
                totalPrice = totalPrice - (preBasketItem.getPrice() - price);
                totalQuantity = totalQuantity - (preBasketItem.getQuantity() - quantity);
            }
            basketDTO.setBasketQuantity(totalQuantity);
            basketDTO.setBasketPrice(totalPrice);
            CartDAO cartDAO = new CartDAO();
            cartDAO.updateCart(cartDTO, basketDTO, basketItemDTO, preBasketItem, preBasket).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Intent intent = new Intent(FoodDetailActivity.this, RestaurantMenuActivity.class);
                    intent.putExtra("restaurantID", restaurantID);
                    startActivity(intent);
                }
            });
        } else if (preCart != null && preBasket == null && preBasketItem == null) {
            //same cart but diffrent restaurant
            System.out.println("vao 3");
            basketItemDTO = new BasketItemDTO();
            basketItemDTO.setQuantity(quantity);
            basketItemDTO.setPrice(price);
            basketDTO = new BasketDTO();
            basketDTO.setRestaurantsInfo(restaurantDTO);
            basketDTO.setBasketQuantity(quantity);
            basketDTO.setBasketPrice(price);
            CartDAO cartDAO = new CartDAO();
            cartDAO.updateCartSameUser(cartDTO, basketDTO, basketItemDTO, foodDTO, preCart).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Intent intent = new Intent(FoodDetailActivity.this, RestaurantMenuActivity.class);
                    intent.putExtra("restaurantID", restaurantID);
                    startActivity(intent);
                }
            });
        } else {
            System.out.println("vao 1");
            UserDAO userDAO = new UserDAO();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                userDAO.getUserById(user.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserDTO userDTO = documentSnapshot.get("userInfo", UserDTO.class);
                        basketItemDTO = new BasketItemDTO();
                        basketItemDTO.setQuantity(quantity);
                        basketItemDTO.setPrice(price);
                        basketDTO = new BasketDTO();
                        basketDTO.setRestaurantsInfo(restaurantDTO);
                        basketDTO.setBasketQuantity(quantity);
                        basketDTO.setBasketPrice(price);
                        cartDTO = new CartDTO();
                        cartDTO.setUserInfo(userDTO);
                        CartDAO cartDAO = new CartDAO();
                        cartDAO.addToCart(cartDTO, basketDTO, basketItemDTO, foodDTO).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Intent intent = new Intent(FoodDetailActivity.this, RestaurantMenuActivity.class);
                                intent.putExtra("restaurantID", restaurantID);
                                startActivity(intent);
                            }
                        });
                    }
                });
            }
        }

    }

    private void createOrder() {
        CartDAO cartDAO = new CartDAO();
//        cartDAO.getCart
    }


}