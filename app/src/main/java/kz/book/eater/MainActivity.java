package kz.book.eater;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private User user = new User();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadUser();
        tabLayout = findViewById(R.id.tabLayout);
        JsonReader.setContext(this);
        tabLayout.performClick(0);
        setFragment(new HomeFragment());
    }
    public void setFragment(Fragment fragment) {
        try {
            tabLayout.setPageInterface((PageInterface) fragment);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
        } catch (ClassCastException e) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
        }
    }
    public void getPageDescription() {
        BaseFragment.setPageDescription(tabLayout.getPageDescriptions());
    }
    public void hideTab() {
        tabLayout.setVisibility(View.GONE);
    }
    public void showTab() {
        tabLayout.setVisibility(View.VISIBLE);
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    private void loadUser() {
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(new File(getFilesDir(), "user.ser")))) {
            user = (User) is.readObject();
        } catch (IOException e) {} catch (ClassNotFoundException e) {}
    }
    private void saveUser() {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File(getFilesDir(), "user.ser")))) {
            os.writeObject(user);
        } catch (IOException e) {}
    }
    @Override
    protected void onPause() {
        super.onPause();
        saveUser();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveUser();
    }
}