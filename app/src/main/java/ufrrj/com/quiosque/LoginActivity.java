package ufrrj.com.quiosque;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private String ACTION_LOGIN_FAIL = "ufrrj.com.quiosque.action.LOGIN_FAIL";
    private String ACTION_COOKIE_EXPIRED = "ufrrj.com.quiosque.action.COOKIE_EXPIRED";

    private Button signin;
    private EditText matricula, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        matricula = (EditText) findViewById(R.id.login_matricula);
        password = (EditText) findViewById(R.id.login_password);
        signin = (Button) findViewById(R.id.login_signin);
        signin.setOnClickListener(this);

        // Identificando action
        if (ACTION_LOGIN_FAIL == getIntent().getAction()){
            password.requestFocus();
            Toast.makeText(this, "Matrícula ou senha inválidos", Toast.LENGTH_SHORT)
                    .show();
        } else if (ACTION_COOKIE_EXPIRED == getIntent().getAction()){
            Toast.makeText(this, "Cookie expirou, realize o login novamente", Toast.LENGTH_SHORT)
                    .show();
        }

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.login_signin:
                // Chamando o meotodo estático para login
                QuiosqueService.startActionLogin(this, matricula.getText().toString(), password.getText().toString());
                // Terminando a activity
                finish();
                break;
        }
    }

}
