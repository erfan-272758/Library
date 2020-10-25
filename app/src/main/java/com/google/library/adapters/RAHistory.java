package com.google.library.adapters;

import android.annotation.SuppressLint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.library.DataBase;
import com.google.library.R;
import com.google.library.dataclass.Book;
import com.google.library.dataclass.BorrowLand;
import com.google.library.dataclass.Person;
import com.google.library.dataclass.Share;
import com.google.library.ui.AnimationListener;
import com.google.library.ui.Design;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class RAHistory extends RecyclerView.Adapter<RAHistory.Holder> implements View.OnClickListener {
    AppCompatActivity context;
    public ArrayList<Book> books;
    public ArrayList<Person> people;
    public ArrayList<BorrowLand> borrowLands;
    public ArrayList<Share> shares;
    float dn;
    boolean historyBook;
    boolean shareHistory;
    String id;
    public RAHistory(AppCompatActivity context,boolean historyBook,boolean shareHistory,String id) {
        this.context = context;
        this.historyBook = historyBook;
        this.shareHistory = shareHistory;
        this.id = id;
        if (!shareHistory){
            borrowLands = new ArrayList<>();
            if (historyBook)
                people = new ArrayList<>();
            else
                books = new ArrayList<>();
        }
        else
            shares = new ArrayList<>();
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        dn = dm.density;
    }

    public void updateBooks(ArrayList<BorrowLand> newBorrowLand,ArrayList<Book> newBooks){
        borrowLands.clear();
        borrowLands.addAll(newBorrowLand);
        books.clear();
        books.addAll(newBooks);
        notifyDataSetChanged();
    }
    public void updatePersons(ArrayList<BorrowLand> newBorrowLand,ArrayList<Person> newPeople){
        borrowLands.clear();
        borrowLands.addAll(newBorrowLand);
        people.clear();
        people.addAll(newPeople);
        notifyDataSetChanged();
    }
    public void updateShares(ArrayList<Share> newShares){
        shares.clear();
        shares.addAll(newShares);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_list,parent,false);
        return new Holder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Log.i("check_borrow","position " + position);

        if (historyBook)
            bindHolderPerson(holder,position);
        else if (!shareHistory)
            bindHolderBook(holder,position);
        else
            bindHolderShare(holder,position);
        if (!shareHistory){
            BorrowLand bl = borrowLands.get(position);
            holder.setBl(bl);
            holder.tv_borrow.setText(bl.timeBorrow.toString());
            if (bl.timeLand != null){
                holder.ll_land.setVisibility(View.VISIBLE);
                holder.tv_land.setText(bl.timeLand.toString());
            }
            else {
                holder.ll_land.setVisibility(View.GONE);
            }
            if (bl.show)
                holder.cv_borrow.setTranslationY(0);
            else
                holder.cv_borrow.setTranslationY(dn * -95);
        }else {
            Share s = shares.get(position);
            holder.setS(s);
            holder.tv_borrow.setText(s.timeStart.toString());
            holder.ll_land.setVisibility(View.VISIBLE);
            holder.tv_land.setText(s.timeEnd.toString());
            if (s.show)
                holder.cv_borrow.setTranslationY(0);
            else
                holder.cv_borrow.setTranslationY(dn * -95);
        }
    }

    @SuppressLint("SetTextI18n")
    private void bindHolderShare(Holder holder, int position) {
        holder.tv_share.setText(shares.get(position).type + " ماهه ");
        holder.cv_share.setTag(holder);
        holder.cv_share.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    private void bindHolderPerson(Holder holder, int position) {
        Person person = people.get(position);
        holder.tv_name.setText(person.firstName + " " + person.lastName);
        holder.tv_idCard.setText(person.idCard);
        holder.cv_person.setTag(holder);
        holder.tv_phone.setText(person.telPhone);
        holder.cv_person.setTag(holder);
        holder.cv_person.setOnClickListener(this);
    }

    private void bindHolderBook(Holder holder, int position) {
        Book book = books.get(position);
        Design.showImage(context,holder.iv,book.imageUri,holder.iv,R.drawable.book,null);
        holder.tv_name.setText(book.name);
        holder.tv_barcode.setText(book.barcode);
        if (book.exist){
            holder.tv_state.setText("موجود است");
            holder.tv_state.setTextColor(ContextCompat.getColor(context, R.color.colorGray));
        }else {
            holder.tv_state.setTextColor(ContextCompat.getColor(context, R.color.colorDarkRead));
            holder.tv_state.setText("موجود نیست");
        }
        String[] arr = context.getResources().getStringArray(R.array.spinner_type_book);
        String type = "انتشارات "+arr[book.type -1];
        String major = "رشته ";
        arr = context.getResources().getStringArray(R.array.spinner_major);
        if (book.major != Book.MINI_O && book.major != Book.MINI_T && book.major !=Book.OMOUMI){
            major += arr[book.major -1];
        }else {
            major = arr[book.major -1];
        }
        holder.tv_major.setText(major);
        holder.tv_year.setText(book.year.trim());
        holder.tv_type.setText(type);
        holder.cv_book.setTag(holder);
        holder.cv_book.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        if (shareHistory)
            return shares.size();
        return  borrowLands.size();
    }

    @Override
    public void onClick(View view) {
        Holder holder = (Holder) view.getTag();
        boolean c = shareHistory ? holder.s.show : holder.bl.show;
        if (!c) {
            holder.cv_borrow.startAnimation(holder.card_in);
        } else {
            holder.cv_borrow.startAnimation(holder.card_out);
        }
    }

    public class Holder extends RecyclerView.ViewHolder {
        MaterialCardView cv_book,cv_person,cv_borrow,cv_share;
        MaterialTextView tv_borrow,tv_land;
        LinearLayout ll_land;
        ImageView iv;
        TextView tv_name,tv_barcode,tv_state,tv_type,tv_year, tv_major,tv_idCard,tv_phone,tv_share;
        BorrowLand bl;
        Share s;
        Animation card_in,card_out;
        public Holder(@NonNull View itemView) {
            super(itemView);
            cv_book = itemView.findViewById(R.id.cv_book_list);
            cv_person = itemView.findViewById(R.id.cv_peron_list);
            cv_borrow = itemView.findViewById(R.id.cv_borrow_land);
            cv_share = itemView.findViewById(R.id.cv_share);
            tv_borrow = itemView.findViewById(R.id.tv_timeBorrow);
            tv_land = itemView.findViewById(R.id.tv_timeLand);
            ll_land = itemView.findViewById(R.id.ll_land);
            card_in = AnimationUtils.loadAnimation(context, R.anim.card_in);
            card_out = AnimationUtils.loadAnimation(context, R.anim.card_out);
            card_out.setAnimationListener(new AnimationListener(){
                @Override
                public void onAnimationEnd(Animation animation) {
                    cv_borrow.setTranslationY(-95 * dn);
                    if (!shareHistory)
                        bl.show = false;
                    else
                        s.show = false;
                }
            });
            card_in.setAnimationListener(new AnimationListener(){
                @Override
                public void onAnimationStart(Animation animation) {
                    cv_borrow.setTranslationY(0);
                    if (!shareHistory)
                        bl.show = true;
                    else
                        s.show = true;
                }
            });
            if (historyBook){
                tv_idCard = itemView.findViewById(R.id.tv_idCard);
                tv_name = itemView.findViewById(R.id.tv_nameP);
                tv_phone = itemView.findViewById(R.id.tv_phone);
                cv_book.setVisibility(View.GONE);
                cv_share.setVisibility(View.GONE);
            }else if (!shareHistory){
                iv = itemView.findViewById(R.id.image);
                tv_name = itemView.findViewById(R.id.tv_name);
                tv_barcode = itemView.findViewById(R.id.tv_barcode);
                tv_state = itemView.findViewById(R.id.tv_state);
                tv_type = itemView.findViewById(R.id.tv_type);
                tv_year = itemView.findViewById(R.id.tv_year);
                tv_major = itemView.findViewById(R.id.tv_major);
                tv_name = itemView.findViewById(R.id.tv_name);
                cv_share.setVisibility(View.GONE);
                cv_person.setVisibility(View.GONE);
            }else {
                tv_share = itemView.findViewById(R.id.tv_typeShare);
                cv_person.setVisibility(View.GONE);
                cv_book.setVisibility(View.GONE);
            }
        }

        public void setS(Share s) {
            this.s = s;
        }

        public void setBl(BorrowLand bl) {
            this.bl = bl;
        }
      }
}
