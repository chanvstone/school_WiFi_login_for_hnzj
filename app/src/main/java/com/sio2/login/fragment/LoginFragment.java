package com.sio2.login.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.sio2.login.MainActivity;
import com.sio2.login.MainViewModel;
import com.sio2.login.R;
import com.sio2.login.account.Account;
import com.sio2.login.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private FragmentLoginBinding binding;
    private MainViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        init_editText();
        init_button();
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.loadAccount();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_network:
                viewModel.getWiFiInterface();
                break;
            case R.id.btn_save_account:
                getDataFromView();
                break;
        }
    }

    private void init_editText() {
        viewModel.ip.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.editTextIp.setText(s);
            }
        });

        viewModel.account.observe(getViewLifecycleOwner(), new Observer<Account>() {
            @Override
            public void onChanged(Account account) {
                binding.editTextUsername.setText(account.getUsername());
                binding.editTextPassword.setText(account.getPassword());
                switch (account.getProvider()) {
                    case "@cmcc":
                        binding.chipGroupProvider.check(R.id.chip_cmcc);
                        break;
                    case "@telcom":
                        binding.chipGroupProvider.check(R.id.chip_telcom);
                        break;
                    case "@unicom":
                        binding.chipGroupProvider.check(R.id.chip_unicom);
                        break;
                    case "@tel":
                        binding.chipGroupProvider.check(R.id.chip_tel);
                        break;
                }
            }
        });
    }

    private void init_button() {
        binding.btnGetNetwork.setOnClickListener(this);
        binding.btnSaveAccount.setOnClickListener(this);
    }

    private void getDataFromView() {
        Account newAccount = new Account();
        newAccount.setUsername(binding.editTextUsername.getText().toString());
        newAccount.setPassword(binding.editTextPassword.getText().toString());
        switch (binding.chipGroupProvider.getCheckedChipId()) {
            case R.id.chip_cmcc:
                newAccount.setProvider("@cmcc");
                break;
            case R.id.chip_telcom:
                newAccount.setProvider("@telcom");
                break;
            case R.id.chip_unicom:
                newAccount.setProvider("@unicom");
                break;
            case R.id.chip_tel:
                newAccount.setProvider("@tel");
                break;
        }
        viewModel.ip.postValue(binding.editTextIp.getText().toString());
        viewModel.account.postValue(newAccount);
        viewModel.saveAccount();
    }

    private void setDataToView() {
        binding.editTextUsername.setText(viewModel.account.getValue().getUsername());
        binding.editTextPassword.setText(viewModel.account.getValue().getPassword());
        switch (viewModel.account.getValue().getProvider()) {
            case "@cmcc":
                binding.chipGroupProvider.check(R.id.chip_cmcc);
                break;
            case "@telcom":
                binding.chipGroupProvider.check(R.id.chip_telcom);
                break;
            case "@unicom":
                binding.chipGroupProvider.check(R.id.chip_unicom);
                break;
            case "@tel":
                binding.chipGroupProvider.check(R.id.chip_tel);
                break;
        }
    }


}
