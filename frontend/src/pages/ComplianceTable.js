import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, TablePagination } from '@mui/material';
import config from '../config/index';

const ComplianceTable = () => {
  const [data, setData] = useState([]);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  useEffect(() => {
    const fetchData = async () => {
  
      const user = JSON.parse(localStorage.getItem("userDetails"));
      console.log("user", user);

      try {
        if (user) {
          const response = await axios.get(`${config.baseURL}/reservation/employer/${user.employerId}/employee/${user.id}/compliance`);
          setData(response.data);
        }
      } catch (error) {
        console.error('Error fetching data:', error);
      }

    };

    fetchData();
  }, []);

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const slicedData = data.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage);

  return (
    <>
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Week Start Date</TableCell>
              <TableCell>GTD Dates</TableCell>
              <TableCell>Self Reservation Dates</TableCell>
              <TableCell>MOP Met</TableCell>
              <TableCell>Preemptable</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {slicedData.map((row) => (
              <TableRow key={row.weekStartDate}>
                <TableCell>{row.weekStartDate}</TableCell>
                <TableCell>{row.gtdDates.join(', ')}</TableCell>
                <TableCell>{row.selfReservationDates.join(', ')}</TableCell>
                <TableCell>{row.mopMet ? 'Yes' : 'No'}</TableCell>
                <TableCell>{row.preemptable ? 'Yes' : 'No'}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      <TablePagination
        rowsPerPageOptions={[10, 25, 50]}
        component="div"
        count={data.length}
        rowsPerPage={rowsPerPage}
        page={page}
        onPageChange={handleChangePage}
        onRowsPerPageChange={handleChangeRowsPerPage}
      />
    </>
  );
};

export default ComplianceTable;
