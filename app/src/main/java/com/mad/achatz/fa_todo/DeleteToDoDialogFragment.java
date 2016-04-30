package com.mad.achatz.fa_todo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;


public class DeleteToDoDialogFragment extends DialogFragment {

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 */
	public interface OnDeleteComfirmedListener {
		void onDeleteConfirmed();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getActivity().getString(R.string.dialog_delete_todo_title))
		       .setMessage(R.string.dialog_delete_todo_description)
		       .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				   @Override
				   public void onClick(DialogInterface dialog, int which) {
					   try {
						   ((OnDeleteComfirmedListener) getActivity()).onDeleteConfirmed();
					   } catch (Exception e) {
						   e.printStackTrace();
					   }
				   }
			   })
		       .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				   @Override
				   public void onClick(DialogInterface dialog, int which) {

				   }
			   });
		       
		// Create the AlertDialog object and return it
		return builder.create();
    }
	
}
