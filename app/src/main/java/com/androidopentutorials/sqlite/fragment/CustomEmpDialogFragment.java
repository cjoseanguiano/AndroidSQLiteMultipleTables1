package com.androidopentutorials.sqlite.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidopentutorials.sqlite.MainActivity;
import com.androidopentutorials.sqlite.R;
import com.androidopentutorials.sqlite.db.DepartmentDAO;
import com.androidopentutorials.sqlite.db.EmployeeDAO;
import com.androidopentutorials.sqlite.to.Department;
import com.androidopentutorials.sqlite.to.Employee;

public class CustomEmpDialogFragment extends DialogFragment {

	// UI references
	private EditText empNameEtxt;
	private EditText empSalaryEtxt;
	private EditText empDobEtxt;
	private Spinner deptSpinner;
	private LinearLayout submitLayout;

	private Employee employee;

	EmployeeDAO employeeDAO;
	ArrayAdapter<Department> adapter;

	public static final String ARG_ITEM_ID = "emp_dialog_fragment";
	
	private static final SimpleDateFormat formatter = new SimpleDateFormat(
			"yyyy-MM-dd", Locale.ENGLISH);
	
	/*
	 * Callback used to communicate with EmpListFragment to notify the list adapter.
	 * MainActivity implements this interface and communicates with EmpListFragment.
	 */
	public interface CustomEmpDialogFragmentListener {
		void onFinishDialog();
	}

	public CustomEmpDialogFragment() {

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		employeeDAO = new EmployeeDAO(getActivity());

		Bundle bundle = this.getArguments();
		employee = bundle.getParcelable("selectedEmployee");

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		View customDialogView = inflater.inflate(R.layout.fragment_add_emp,
				null);
		builder.setView(customDialogView);

		empNameEtxt = (EditText) customDialogView.findViewById(R.id.etxt_name);
		empSalaryEtxt = (EditText) customDialogView
				.findViewById(R.id.etxt_salary);
		empDobEtxt = (EditText) customDialogView.findViewById(R.id.etxt_dob);
		deptSpinner = (Spinner) customDialogView
				.findViewById(R.id.spinner_dept);
		submitLayout = (LinearLayout) customDialogView
				.findViewById(R.id.layout_submit);
		submitLayout.setVisibility(View.GONE);
		setValue();

		builder.setTitle(R.string.update_emp);
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.update,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						try {
							employee.setDateOfBirth(formatter.parse(empDobEtxt.getText().toString()));
						} catch (ParseException e) {
							Toast.makeText(getActivity(),
									"Invalid date format!",
									Toast.LENGTH_SHORT).show();
							return;
						}
						employee.setName(empNameEtxt.getText().toString());
						employee.setSalary(Double.parseDouble(empSalaryEtxt
								.getText().toString()));
						Department dept = (Department) adapter
								.getItem(deptSpinner.getSelectedItemPosition());
						employee.setDepartment(dept);
						long result = employeeDAO.update(employee);
						if (result > 0) {
							MainActivity activity = (MainActivity) getActivity();
							activity.onFinishDialog();
						} else {
							Toast.makeText(getActivity(),
									"Unable to update employee",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();

					}
				});

		AlertDialog alertDialog = builder.create();

		return alertDialog;
	}

	private void setValue() {
		DepartmentDAO departmentDAO = new DepartmentDAO(getActivity());

		List<Department> departments = departmentDAO.getDepartments();
		adapter = new ArrayAdapter<Department>(getActivity(),
				android.R.layout.simple_list_item_1, departments);
		deptSpinner.setAdapter(adapter);
		int pos = adapter.getPosition(employee.getDepartment());

		if (employee != null) {
			empNameEtxt.setText(employee.getName());
			empSalaryEtxt.setText(employee.getSalary() + "");
			empDobEtxt.setText(formatter.format(employee.getDateOfBirth()));
			deptSpinner.setSelection(pos);
		}
	}
}
