package com.masai.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.masai.exceptions.TripBookingException;
import com.masai.model.BillDetails;
import com.masai.model.Customer;
import com.masai.model.Driver;
import com.masai.model.TripBooking;
import com.masai.model.TripBookingDTO;
import com.masai.repository.CustomerDao;
import com.masai.repository.DriverDao;
import com.masai.repository.TripBookingDao;

@Service
public class TripBookingServiceImpl implements TripBookingService {

	@Autowired
	private TripBookingDao tdao;

	@Autowired
	private DriverDao ddao;

	@Autowired
	private CustomerDao cdao;

	@Override
	public TripBooking insertTripBooking(TripBookingDTO tripBooking) throws TripBookingException {
		// TODO Auto-generated method stub

		Optional<Customer> customer = cdao.findById(tripBooking.getCustomerId());

		if (customer.isPresent()) {
			Customer c = customer.get();
			TripBooking tripB = new TripBooking();

			tripB.setFromLocation(tripBooking.getFromLocation());
			tripB.setToLocation(tripBooking.getToLocation());
			tripB.setFromDateTime(tripBooking.getFromTime());
			tripB.setToDateTime(tripBooking.getToTime());
			int min = 10;
			int max = 100;
			float distance = (float) Math.floor(Math.random() * (max - min + 1) + min);
			tripB.setDistanceInKm(distance);

			tripB.setCustomer(c);
			List<Driver> driverlist = ddao.findAll();

			Driver driver = null;
			for (int i = 0; i < driverlist.size(); i++) {
				if (driverlist.get(i).getAvailablity() == true) {
					driver = driverlist.get(i);
					break;
				}
			}

			if (driver == null)
				throw new TripBookingException("No Driver Available at the moment");

			tripB.setDriver(driver);
			driver.getTripBookingList().add(tripB);
			driver.setAvailablity(false);

			c.getTripBooking().add(tripB);

			tdao.save(tripB);

			return tripB;

		} else {
			throw new TripBookingException("Customer not found with id " + tripBooking.getCustomerId());
		}
	}

	@Override
	public String deleteTripBooking(Integer customerId) throws TripBookingException {

//	  Optional<TripBooking> trip = tdao.findById(tripBooking.getTripId());
//	  if(trip.isPresent()) {
//		  tdao.delete(trip.get());
//	  }
//	  throw new TripBookingException("No trip found");
		Optional<Customer> customer = cdao.findById(customerId);
		if (customer.isPresent()) {
			Customer cus = customer.get();
			List<TripBooking> tripB = cus.getTripBooking();

			if (tripB.size() > 0) {
				if (tripB.get(tripB.size() - 1).isStatus() == false) {
					Driver driver = tripB.get(tripB.size() - 1).getDriver();
					driver.setAvailablity(true);
					ddao.save(driver);
					tripB.remove(tripB.size() - 1);
					cdao.save(cus);

					return "Trip cancelled Successfully";
				}
			}
			return "No Trip found";

		} else {
			throw new TripBookingException("Customer not found with id :" + customerId);
		}

	}

	@Override
	public List<TripBooking> viewAllTripsCustomer(int customerId) throws TripBookingException {
		// TODO Auto-generated method stub
		Optional<Customer> customer = cdao.findById(customerId);

		if (customer.isPresent()) {
			Customer c = customer.get();
			List<TripBooking> tripBooking = c.getTripBooking();
			return tripBooking;
		}

		throw new TripBookingException("No trip for this customer having id : " + customerId);

	}

	@Override
	public String calculateBill(Integer driverId) throws TripBookingException {
		Optional<Driver> driver = ddao.findById(driverId);
		if (driver.isPresent()) {
			Driver cabDriver = driver.get();
			List<TripBooking> customerTripList = cabDriver.getTripBookingList();

			if (customerTripList.size() == 0)
				throw new TripBookingException("No Trip found");

			TripBooking lastTrip = customerTripList.get(customerTripList.size() - 1);
			if (lastTrip.isStatus() == true)
				throw new TripBookingException("All Trips Completed");

			float ratePerkms = (float) cabDriver.getCab().getRatePerKms();
			float distance = lastTrip.getDistanceInKm();

			lastTrip.setBill(distance * ratePerkms);
			cabDriver.setAvailablity(true);
			lastTrip.setStatus(true);

			ddao.save(cabDriver);

			return "Bill is " + lastTrip.getBill();

		} else {
			throw new TripBookingException("Driver does not exist with id" + driverId);
		}

	}

	@Override
	public BillDetails generateBill(Integer customerId, Integer tripBookingId) throws TripBookingException {
		// TODO Auto-generated method stub
		Customer customer = cdao.findById(customerId).get();
		if (customer == null) {
			throw new TripBookingException("customer not found");
		}
		TripBooking tripB = tdao.findById(tripBookingId).get();
		if (tripB == null)
			throw new TripBookingException("trip with given id does not exist");

		if (customerId == tripB.getCustomer().getCustomerId()) {
			if (tripB.isStatus() == false)
				throw new TripBookingException("Trip not completed yet");

			BillDetails billDetails = new BillDetails();
			billDetails.setDistance(tripB.getDistanceInKm());
			billDetails.setRatePerKms(tripB.getDriver().getCab().getRatePerKms());
			billDetails.setTotalBill(tripB.getBill());
			return billDetails;
		}

		throw new TripBookingException("User not Verified");

	}

	@Override
	public List<TripBooking> getAllTrips() throws TripBookingException {
		// TODO Auto-generated method stub

		List<TripBooking> alltrip = tdao.findAll();
		if (alltrip.size() > 0) {
			return alltrip;
		}
		throw new TripBookingException("No trip found");
	}

}
