package calificado.guerra.tecsup.edu.pe.labcalificado3.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import calificado.guerra.tecsup.edu.pe.labcalificado3.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





    }

    public void gotoLogin(View view){

        startActivity(new Intent(this, Bienvenido.class));
        finish();

    }

}
