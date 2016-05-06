package com.mad.achatz.fa_todo;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;


public class ContactListAdapter extends ArrayAdapter {

    private ContactListClickListener contactListClickListener;

    public ContactListAdapter(Context context, List<Integer> objects) {
        super(context, R.layout.contact_list_item, objects);
    }

    public void setContactListClickListener(ContactListClickListener contactListClickListener) {
        this.contactListClickListener = contactListClickListener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int contactId = (Integer) getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.contact_list_item, null);
        }

        // Alle View Elemente bereit legen
        TextView nameTextView = (TextView) convertView.findViewById(R.id.contact_list_item_name_textview);
        ImageButton smsButton = (ImageButton) convertView.findViewById(R.id.textmessage_contact_imagebutton);
        ImageButton emailButton = (ImageButton) convertView.findViewById(R.id.email_contact_imagebutton);
        ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.delete_contact_imagebutton);

        // Setze sms und email button unsichtbar per default
        smsButton.setVisibility(View.GONE);
        emailButton.setVisibility(View.GONE);

        // Arrays mit abzufragenden Spalten die wir aus der Datenbank abrufen wollen
        String[] phoneColumns = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        String[] mailColumns = new String[]{ContactsContract.CommonDataKinds.Email.ADDRESS};

        // WHERE Anweisung um nur den Kontakt mit unserer ID auszuwählen.
        String whereClause = ContactsContract.CommonDataKinds.Phone.CONTACT_ID +"=" + contactId;

        // Cursor mit Datenbankergebnissen anlegen
        Cursor phoneCursor = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, phoneColumns, whereClause, null, null);
        Cursor mailCursor = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, mailColumns, whereClause, null, null);

        // Checken, ob Ergebnis vorhanden ist
        if (phoneCursor.moveToFirst()) {
            // Indices für unserer Spalten bereitlegen
            int nameColumnIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int hasNumberColumnIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER);
            int numberColumnIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            // Name holen und in Textview eintragen
            String name = phoneCursor.getString(nameColumnIndex);
            if (name != null) {
                nameTextView.setText(name);
            }

            // Nummer holen, String muss final sein, weil wir ihn in innerer Klasse des Onclicklistener benutzen
            final String number = phoneCursor.getString(numberColumnIndex);
            boolean hasNumber = phoneCursor.getInt(hasNumberColumnIndex) > 0;

            // Onclicklistener einrichten, falls Nummer vorhanden
            if (hasNumber && contactListClickListener != null) {
                smsButton.setVisibility(View.VISIBLE);
                smsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contactListClickListener.onSmsClicked(position, number);
                    }
                });
            }
        }


        // Jetzt noch die email
        if(mailCursor.moveToFirst()) {
            int emailAddressColumnIndex = mailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);

            final String emailAddress = mailCursor.getString(emailAddressColumnIndex);

            // Onclicklistener einrichten, falls Nummer vorhanden
            if (emailAddress != null && !emailAddress.isEmpty() && contactListClickListener != null) {
                emailButton.setVisibility(View.VISIBLE);
                emailButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contactListClickListener.onEmailClicked(position, emailAddress);
                    }
                });
            }
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contactListClickListener != null)
                    contactListClickListener.onDeleteClicked(position);
            }
        });

        phoneCursor.close();
        mailCursor.close();

        return convertView;
    }



    public interface ContactListClickListener {
        void onSmsClicked(int position, String number);
        void onEmailClicked(int position, String address);
        void onDeleteClicked(int position);
    }

}
