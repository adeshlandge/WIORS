import React, { useEffect, useState } from 'react';
import axios from 'axios';
import ReservationHelperTable from '../pages/TablePage';
import { Button, Snackbar } from '@mui/material';
import { useSnackbar } from 'notistack';
import config from '../config/index'

const ReservationTable = () => {
  const [data, setData] = useState([]);
  const { enqueueSnackbar } = useSnackbar();

  const fetchData = async () => {
    const user = JSON.parse(localStorage.getItem("userDetails"));

    try {
      if (user) {
        const response = await axios.get(`${config.baseURL}/reservation/employer/${user.employerId}/employee/${user.id}/all`);
        setData(response.data);
      }
    } catch (error) {
      console.error('Error fetching data:', error);
    }
  };
  
  useEffect(() => {
    const user = JSON.parse(localStorage.getItem("userDetails"));

    const fetchData = async () => {
      try {
        if (user) {
          const response = await axios.get(`${config.baseURL}/reservation/employer/${user.employerId}/employee/${user.id}/all`);
          setData(response.data);
        }
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };

    fetchData();
  }, []);

  const deleteAllReservations = async () => {
    const user = JSON.parse(localStorage.getItem("userDetails"));

    try {
      await axios.delete(`${config.baseURL}/reservation/employer/${user.employerId}/employee/${user.id}/all`);
      setData([]); // Clear the data array after successful deletion
      enqueueSnackbar('All reservations deleted.', { variant: 'success' });
    } catch (error) {
      console.error('Error deleting reservations:', error);
      enqueueSnackbar('Error deleting reservations.', { variant: 'error' });
    }
  };

  return (
    <>
      <Button variant="contained" color="primary" onClick={deleteAllReservations}>
        Delete All Reservations
      </Button>
      <ReservationHelperTable data={data}  refreshData={fetchData}/>
    </>
  );
};

export default ReservationTable;
