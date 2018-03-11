package com.androidopentutorials.sqlite.fragment;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.androidopentutorials.sqlite.R;
import com.androidopentutorials.sqlite.db.DepartmentDAO;
import com.androidopentutorials.sqlite.db.EmployeeDAO;
import com.androidopentutorials.sqlite.to.Department;
import com.androidopentutorials.sqlite.to.Employee;

public class EmpAddFragment extends Fragment implements OnClickListener {

	// UI references
	private EditText empNameEtxt;
	private EditText empSalaryEtxt;
	private EditText empDobEtxt;
	private Spinner deptSpinner;
	private Button addButton;
	private Button resetButton;

	private static final SimpleDateFormat formatter = new SimpleDateFormat(
			"yyyy-MM-dd", Locale.ENGLISH);

	DatePickerDialog datePickerDialog;
	Calendar dateCalendar;

	Employee employee = null;
	private EmployeeDAO employeeDAO;
	private DepartmentDAO departmentDAO;
	private GetDeptTask task;
	private AddEmpTask addEmpTask;

	public static final String ARG_ITEM_ID = "emp_add_fragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		employeeDAO = new EmployeeDAO(getActivity());
		departmentDAO = new DepartmentDAO(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_add_emp, container,
				false);

		findViewsById(rootView);

		setListeners();

		// Used for orientation change
		/*
		 * After entering the fields, change the orientation.
		 * NullPointerException occurs for date. This piece of code avoids it.
		 */
		if (savedInstanceState != null) {
			dateCalendar = Calendar.getInstance();
			if (savedInstanceState.getLong("dateCalendar") != 0)
				dateCalendar.setTime(new Date(savedInstanceState
						.getLong("dateCalendar")));
		}

		// asynchronously retrieves department from table and sets it in Spinner
		task = new GetDeptTask(getActivity());
		task.execute((Void) null);

		return rootView;
	}

	private void setListeners() {
		empDobEtxt.setOnClickListener(this);
		Calendar newCalendar = Calendar.getInstance();
		datePickerDialog = new DatePickerDialog(getActivity(),
				new OnDateSetListener() {

					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						dateCalendar = Calendar.getInstance();
						dateCalendar.set(year, monthOfYear, dayOfMonth);
						empDobEtxt.setText(formatter.format(dateCalendar
								.getTime()));
					}

				}, newCalendar.get(Calendar.YEAR),
				newCalendar.get(Calendar.MONTH),
				newCalendar.get(Calendar.DAY_OF_MONTH));

		addButton.setOnClickListener(this);
		resetButton.setOnClickListener(this);
	}

	protected void resetAllFields() {
		empNameEtxt.setText("");
		empSalaryEtxt.setText("");
		empDobEtxt.setText("");
		if (deptSpinner.getAdapter().getCount() > 0)
			deptSpinner.setSelection(0);
	}

	private void setEmployee() {
		employee = new Employee();
		employee.setName(empNameEtxt.getText().toString());
		employee.setSalary(Double.parseDouble(empSalaryEtxt.getText()
				.toString()));
		if (dateCalendar != null)
			employee.setDateOfBirth(dateCalendar.getTime());
		Department selectedDept = (Department) deptSpinner.getSelectedItem();
		employee.setDepartment(selectedDept);
	}

	@Override
	public void onResume() {
		getActivity().setTitle(R.string.add_emp);
		getActivity().getActionBar().setTitle(R.string.add_emp);
		super.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (dateCalendar != null)
			outState.putLong("dateCalendar", dateCalendar.getTime().getTime());
	}

	private void findViewsById(View rootView) {
		empNameEtxt = (EditText) rootView.findViewById(R.id.etxt_name);
		empSalaryEtxt = (EditText) rootView.findViewById(R.id.etxt_salary);
		empDobEtxt = (EditText) rootView.findViewById(R.id.etxt_dob);
		empDobEtxt.setInputType(InputType.TYPE_NULL);

		deptSpinner = (Spinner) rootView.findViewById(R.id.spinner_dept);
		addButton = (Button) rootView.findViewById(R.id.button_add);
		resetButton = (Button) rootView.findViewById(R.id.button_reset);
	}

	@Override
	public void onClick(View view) {
		if (view == empDobEtxt) {
			datePickerDialog.show();
		} else if (view == addButton) {
			setEmployee();
			addEmpTask = new AddEmpTask(getActivity());
			addEmpTask.execute((Void) null);
		} else if (view == resetButton) {
			resetAllFields();
		}
	}

	public class GetDeptTask extends AsyncTask<Void, Void, Void> {

		private final WeakReference<Activity> activityWeakRef;
		private List<Department> departments;

		public GetDeptTask(Activity context) {
			this.activityWeakRef = new WeakReference<Activity>(context);
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			departments = departmentDAO.getDepartments();
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			if (activityWeakRef.get() != null
					&& !activityWeakRef.get().isFinishing()) {

				ArrayAdapter<Department> adapter = new ArrayAdapter<Department>(
						activityWeakRef.get(),
						android.R.layout.simple_list_item_1, departments);
				deptSpinner.setAdapter(adapter);

				addButton.setEnabled(true);
			}
		}
	}

	public class AddEmpTask extends AsyncTask<Void, Void, Long> {

		private final WeakReference<Activity> activityWeakRef;

		public AddEmpTask(Activity context) {
			this.activityWeakRef = new WeakReference<Activity>(context);
		}

		@Override
		protected Long doInBackground(Void... arg0) {
			long result = employeeDAO.save(employee);
			return result;
		}

		@Override
		protected void onPostExecute(Long result) {
			if (activityWeakRef.get() != null
					&& !activityWeakRef.get().isFinishing()) {
				if (result != -1)
					Toast.makeText(activityWeakRef.get(), "Employee Saved",
							Toast.LENGTH_LONG).show();
			}
		}
	}
}
