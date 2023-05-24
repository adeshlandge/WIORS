import DateRangePicker from "rsuite/DateRangePicker";
import "rsuite/dist/rsuite.css";
import { Box, Button, Typography } from "@mui/material";
import React, { useState } from "react";
import ComplianceTable from "../ComplianceTable";
import axios from "axios";
import ReservationTable from "../ReservationTable";
import { enqueueSnackbar } from "notistack";
import config from "../../config/index";

const {
  allowedMaxDays,
  allowedDays,
  allowedRange,
  beforeToday,
  afterToday,
  combine,
  after,
} = DateRangePicker;
let today = new Date();
let futureDate = new Date(today.getTime() + 70 * 24 * 60 * 60 * 1000);

const SeatReservation = () => {

  const [selectedDateRange, setSelectedDateRange] = useState([]);

  const handleDateRangeChange = (value) => {
    setSelectedDateRange(value);

  };
  const handleSubmit = async (event) => {
    event.preventDefault();
    try {

      const user = JSON.parse(localStorage.getItem("userDetails"));
      console.log("user", user);
      console.log("Email", user.email);
      console.log("Date range", selectedDateRange);

      const startDate = new Date(selectedDateRange[0]);
      const endDate = new Date(selectedDateRange[1]);

      // Extract the dates
      const options = { timeZone: "America/Los_Angeles" };
      const startDateParts = startDate.toLocaleDateString("en-US", options).split(",")[0].split("/");
      const endDateParts = endDate.toLocaleDateString("en-US", options).split(",")[0].split("/");
      const startDateFormatted = `${startDateParts[2]}-${startDateParts[0].padStart(2, "0")}-${startDateParts[1].padStart(2, "0")}`;
      const endDateFormatted = `${endDateParts[2]}-${endDateParts[0].padStart(2, "0")}-${endDateParts[1].padStart(2, "0")}`;

      console.log("Start Date:", startDateFormatted);
      console.log("End Date:", endDateFormatted);
      const response = await axios.post(`${config.baseURL}/reservation/employer/${user.employerId}/employee/${user.id}`, {

        emailId: user.email,
        startDate: startDateFormatted,
        endDate: endDateFormatted
      });
      enqueueSnackbar("Reservation Successful", { variant: "success" });

      console.log("Selected Date Range:", selectedDateRange);
    } catch (error) {
      enqueueSnackbar("Reservation Failed: " , { variant: "error" });
    
    }

    // TODO: Add functionality to submit the selected value to the server
  };
  const isWeekend = (date) => {
    const day = date.getDay();
    return day === 0 || day === 6; // Sunday (0) or Saturday (6)
  };

  const disabledDate = (date) => {
    return isWeekend(date)
  };

  return (
    <div>
      <Typography
        variant="h4"
        sx={{ marginBottom: "20px", textAlign: "center" }}
      >
        Seat Reservation
      </Typography>
      <Box
        sx={{ display: "flex", flexDirection: "column", alignItems: "center" }}
      >
        <Typography
          variant="h6"
          sx={{ marginBottom: "20px", textAlign: "center" }}
        >
          Select Consecutive Days in a Week
        </Typography>
        <DateRangePicker
          shouldDisableDate={disabledDate}
          value={selectedDateRange}
          onChange={handleDateRangeChange}
        />
        <Button
          sx={{ minWidth: 120, m: 2 }}
          variant="contained"
          onClick={handleSubmit}
        >
          Submit
        </Button>
        <Typography
        variant="h5"
        sx={{ marginBottom: "20px", textAlign: "center" }}
      >
        ComplianceTable
      </Typography>
        <ComplianceTable></ComplianceTable>
      </Box>
    </div>
  );
};

export default SeatReservation;
