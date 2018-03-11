package com.androidopentutorials.sqlite;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.amitshekhar.DebugDB;
import com.androidopentutorials.sqlite.db.DepartmentDAO;
import com.androidopentutorials.sqlite.fragment.CustomEmpDialogFragment.CustomEmpDialogFragmentListener;
import com.androidopentutorials.sqlite.fragment.EmpAddFragment;
import com.androidopentutorials.sqlite.fragment.EmpListFragment;

public class MainActivity extends FragmentActivity implements
		CustomEmpDialogFragmentListener {

	private Fragment contentFragment;
	private EmpListFragment employeeListFragment;
	private EmpAddFragment employeeAddFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		DebugDB.getAddressLog();
		FragmentManager fragmentManager = getSupportFragmentManager();

		DepartmentDAO deptDAO = new DepartmentDAO(this);
		
		//Initially loads departments
		if(deptDAO.getDepartments().size() <= 0)
			deptDAO.loadDepartments();
		
		/*
		 * This is called when orientation is changed.
		 */
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey("content")) {
				String content = savedInstanceState.getString("content");
				if (content.equals(EmpAddFragment.ARG_ITEM_ID)) {
					if (fragmentManager
							.findFragmentByTag(EmpAddFragment.ARG_ITEM_ID) != null) {
						setFragmentTitle(R.string.add_emp);
						contentFragment = fragmentManager
								.findFragmentByTag(EmpAddFragment.ARG_ITEM_ID);
					}
				}
			}
			if (fragmentManager.findFragmentByTag(EmpListFragment.ARG_ITEM_ID) != null) {
				employeeListFragment = (EmpListFragment) fragmentManager
						.findFragmentByTag(EmpListFragment.ARG_ITEM_ID);
				contentFragment = employeeListFragment;
			}
		} else {
			employeeListFragment = new EmpListFragment();
			setFragmentTitle(R.string.app_name);
			switchContent(employeeListFragment, EmpListFragment.ARG_ITEM_ID);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add:
			setFragmentTitle(R.string.add_emp);
			employeeAddFragment = new EmpAddFragment();
			switchContent(employeeAddFragment, EmpAddFragment.ARG_ITEM_ID);

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (contentFragment instanceof EmpAddFragment) {
			outState.putString("content", EmpAddFragment.ARG_ITEM_ID);
		} else {
			outState.putString("content", EmpListFragment.ARG_ITEM_ID);
		}
		super.onSaveInstanceState(outState);
	}

	/*
	 * We consider EmpListFragment as the home fragment and it is not added to
	 * the back stack.
	 */
	public void switchContent(Fragment fragment, String tag) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		while (fragmentManager.popBackStackImmediate())
			;

		if (fragment != null) {
			FragmentTransaction transaction = fragmentManager
					.beginTransaction();
			transaction.replace(R.id.content_frame, fragment, tag);
			// Only EmpAddFragment is added to the back stack.
			if (!(fragment instanceof EmpListFragment)) {
				transaction.addToBackStack(tag);
			}
			transaction.commit();
			contentFragment = fragment;
		}
	}

	protected void setFragmentTitle(int resourseId) {
		setTitle(resourseId);
		getActionBar().setTitle(resourseId);

	}

	/*
	 * We call super.onBackPressed(); when the stack entry count is > 0. if it
	 * is instanceof EmpListFragment or if the stack entry count is == 0, then
	 * we prompt the user whether to quit the app or not by displaying dialog.
	 * In other words, from EmpListFragment on back press it quits the app.
	 */
	@Override
	public void onBackPressed() {
		FragmentManager fm = getSupportFragmentManager();
		if (fm.getBackStackEntryCount() > 0) {
			super.onBackPressed();
		} else if (contentFragment instanceof EmpListFragment
				|| fm.getBackStackEntryCount() == 0) {
			//finish();
			//Shows an alert dialog on quit
			onShowQuitDialog();
		}
	}
	
	public void onShowQuitDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);

		builder.setMessage("Do You Want To Quit?");
		builder.setPositiveButton(android.R.string.yes,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						finish();
					}
				});
		builder.setNegativeButton(android.R.string.no,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		builder.create().show();
	}

	/*
	 * Callback used to communicate with EmpListFragment to notify the list adapter.
	 * Communication between fragments goes via their Activity class.
	 */
	@Override
	public void onFinishDialog() {
		if (employeeListFragment != null) {
			employeeListFragment.updateView();
		}
	}
}
