package com.vs.schoolmessenger.adapter;


import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseViewHolders<V extends ViewDataBinding> extends RecyclerView.ViewHolder {
    private V binding;

    public BaseViewHolders(V binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Object object) {
        binding.setVariable(getVariable(), object);
        binding.executePendingBindings();
    }

    public V getBinding() {
        return binding;
    }

    public abstract int getVariable();
}
