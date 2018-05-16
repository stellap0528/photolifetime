package edu.rosehulman.lewistd.photolifetime.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.rosehulman.lewistd.photolifetime.R;


/**
 * Created by parks8 on 2018-05-03.
 */

public class LoginFragment extends Fragment {

    private static final String FACEBOOK_LOGIN_ERROR = "Facebook login error: ";
    private static final String ERROR = "ERROR";

    private EditText mEmailView;
    private EditText mPasswordView;
    private View mLoginForm;
    private OnLoginListener mLoginListener;
    private Button mEmailLoginButton;
    private boolean mLoggingIn;
    private View mProgressSpinner;


    public LoginFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        mEmailView = (EditText)rootView.findViewById(R.id.email);
        mPasswordView = (EditText)rootView.findViewById(R.id.password);
        mLoginForm = rootView.findViewById(R.id.login_form);

        mEmailLoginButton = (Button)rootView.findViewById(R.id.email_login_button);
        mEmailLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailLogin();
            }
        });

        return rootView;
    }

    public void emailLogin(){
        mEmailView.setError(null);
        mPasswordView.setError(null);

        Log.d("email", "Pressed");
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancelLogin = false;

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(getActivity(), R.string.empty_email_password, Toast.LENGTH_SHORT).show();
            cancelLogin = true;
        } else if(!isEmailValid(email)){
            Toast.makeText(getActivity(), R.string.invalid_email, Toast.LENGTH_SHORT).show();
            cancelLogin = true;
        } else if(!isPasswordValid(password)){
            Toast.makeText(getActivity(), R.string.invalid_password, Toast.LENGTH_SHORT).show();
            cancelLogin = true;
        }

        if(!cancelLogin){
            Log.d("email", "email:  "+email+"   password:  "+password);
            mLoginListener.onLogin(email, password);
        }
    }

    private boolean isEmailValid(String email) {

        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {

        return password.length() > 4;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mLoginListener = (OnLoginListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mLoginListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public interface OnLoginListener {
        void onLogin(String email, String password);

    }
//
//    public void onLoginError(String message) {
//        new AlertDialog.Builder(getActivity())
//                .setTitle(getActivity().getString(R.string.login_error))
//                .setMessage(message)
//                .setPositiveButton(android.R.string.ok, null)
//                .create()
//                .show();
//
//        showProgress(false);
//        mLoggingIn = false;
//    }
//
//    private void showProgress(boolean show) {
//        mProgressSpinner.setVisibility(show ? View.VISIBLE : View.GONE);
//        mLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
//    }


}
