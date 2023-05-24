import React, { useState } from "react";
import { Table, TableHead, TableRow, TableCell, TableBody, TablePagination } from "@mui/material";
import axios from "axios";
import { useSnackbar } from "notistack";
import config from '../config/index';

const ReservationHelperTable = ({ data , refreshData}) => {
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);
  const { enqueueSnackbar } = useSnackbar();

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  const handleDeleteReservation = (id) => {
    axios
      .delete(`${config.baseURL}/reservation/delete/${id}`)
      .then((response) => {
        // Handle the response from the server, e.g., show a success message
        enqueueSnackbar("Reservation deleted successfully.", { variant: "success" });
        refreshData();
        // Optionally, you can update the reservations list after deletion
        // ...
      })
      .catch((error) => {
        // Handle error if the request fails
        enqueueSnackbar("Failed to delete reservation.", { variant: "error" });
        console.error("Error:", error);
      });
  };

  const rowsPerPageOptions = [5, 10, 25];

  if (!data || data.length === 0) {
    return <div>No data available.</div>;
  }

  const emptyRows = rowsPerPage - Math.min(rowsPerPage, data.length - page * rowsPerPage);

  return (
    <>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>ID</TableCell>
            <TableCell>Date</TableCell>
            <TableCell>Employee ID</TableCell>
            <TableCell>Employer ID</TableCell>
            <TableCell>Email</TableCell>
            <TableCell>Is Self Reservation</TableCell>
            <TableCell>Action</TableCell> {/* Added column for delete button */}
          </TableRow>
        </TableHead>
        <TableBody>
          {(rowsPerPage > 0
            ? data.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
            : data
          ).map((row) => (
            <TableRow key={row.id}>
              <TableCell>{row.id}</TableCell>
              <TableCell>{row.date}</TableCell>
              <TableCell>{row.employee.id}</TableCell>
              <TableCell>{row.employee.employerId}</TableCell>
              <TableCell>{row.employee.email}</TableCell>
              <TableCell>{row.isSelfReservation ? "Yes" : "No"}</TableCell>
              <TableCell>
                <button onClick={() => handleDeleteReservation(row.id)}>Delete</button>
              </TableCell>
            </TableRow>
          ))}
          {emptyRows > 0 && (
            <TableRow style={{ height: 53 * emptyRows }}>
              <TableCell colSpan={7} />
            </TableRow>
          )}
        </TableBody>
      </Table>
      <TablePagination
        rowsPerPageOptions={rowsPerPageOptions}
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

export default ReservationHelperTable;
