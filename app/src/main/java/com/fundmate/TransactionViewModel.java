package com.fundmate;

import androidx.lifecycle.ViewModel;
import java.util.List;

public class TransactionViewModel extends ViewModel {
    private List<TransactionModel> transactions;

    public List<TransactionModel> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionModel> transactions) {
        this.transactions = transactions;
    }
}

