package com.androidopentutorials.sqlite.adapter;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.androidopentutorials.sqlite.R;
import com.androidopentutorials.sqlite.to.Employee;

public class EmpListAdapter extends ArrayAdapter<Employee> {

	private Context context;
	List<Employee> employees;

	private static final SimpleDateFormat formatter = new SimpleDateFormat(
			"yyyy-MM-dd", Locale.ENGLISH);
	
	public EmpListAdapter(Context context, List<Employee> employees) {
		super(context, R.layout.list_item, employees);
		this.context = context;
		this.employees = employees;
	}

	private class ViewHolder {
		TextView empIdTxt;
		TextView empNameTxt;
		TextView empDobTxt;
		TextView empSalaryTxt;
		TextView empDeptNameTxt;
	}

	@Override
	public int getCount() {
		return employees.size();
	}

	@Override
	public Employee getItem(int position) {
		return employees.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.list_item, null);
			holder = new ViewHolder();
			
			holder.empIdTxt = (TextView) convertView
					.findViewById(R.id.txt_emp_id);
			holder.empNameTxt = (TextView) convertView
					.findViewById(R.id.txt_emp_name);
			holder.empDobTxt = (TextView) convertView
					.findViewById(R.id.txt_emp_dob);
			holder.empSalaryTxt = (TextView) convertView
					.findViewById(R.id.txt_emp_salary);
			holder.empDeptNameTxt = (TextView) convertView
					.findViewById(R.id.txt_emp_dept);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Employee employee = (Employee) getItem(position);
		holder.empIdTxt.setText(employee.getId() + "");
		holder.empNameTxt.setText(employee.getName());
		holder.empSalaryTxt.setText(employee.getSalary() + "");
		holder.empDeptNameTxt.setText(employee.getDepartment().getName());
		
		holder.empDobTxt.setText(formatter.format(employee.getDateOfBirth()));	
		
		return convertView;
	}

	@Override
	public void add(Employee employee) {
		employees.add(employee);
		notifyDataSetChanged();
		super.add(employee);
	}

	@Override
	public void remove(Employee employee) {
		employees.remove(employee);
		notifyDataSetChanged();
		super.remove(employee);
	}	
}

