import React, { useState, useEffect } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import {
  createUserWithEmailAndPassword,
  sendEmailVerification,
  signOut,
} from "firebase/auth";
import { auth } from "../firebase";
import { TextField, Button, Typography, Link, Box } from "@mui/material";
import {
  FormControl,
  FormControlLabel,
  Radio,
  RadioGroup,
  InputLabel,
  Select,
  MenuItem
} from "@mui/material";

import { useSnackbar } from "notistack";
import axios from "axios";
import config from "../config";

const Signup = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [userType, setUserType] = useState("employee");
  const [name, setName] = useState("");
  const [title, setTitle] = useState(""); // Added title state
  const [description, setDescription] = useState(""); // Added description state
  const [street, setStreet] = useState("");
  const [city, setCity] = useState("San Jose");
  const [state, setState] = useState("CA");
  const [zipCode, setZipCode] = useState("95120");
  const [managerEmail, setManagerEmail] = useState("");

  const [seatingCapacity, setSeatingCapacity] = useState(3);
  const { enqueueSnackbar } = useSnackbar();
  const [employerNames, setEmployerNames] = useState([]);
  const [selectedEmployer, setSelectedEmployer] = useState("");

  // ...

  const onSubmit = async (e) => {
    e.preventDefault();
    console.log("Email:", email);
    console.log("Name:", name);
    console.log("Street:", street);
    console.log("City:", city);
    console.log("State:", state);
    console.log("Zip Code:", zipCode);
    console.log("Manager Email:", managerEmail);

   

    try {
      // Send sign up request to the backend based on the userType
      if (userType === "employee") {
        const employeeRequest = {
          name: name,
          email: email,
          password: password,
          employerId: selectedEmployer.id,
          managerEmailId: managerEmail,
          title: title, // Include the title field
          street: street,
          city: city,
          state: state,
          zip: zipCode,
        };
        await axios.post(
          `${config.baseURL}/register/employee`,
          employeeRequest
        );
        enqueueSnackbar( "Successfully registered", { variant: "success" });

      } else if (userType === "employer") {
        const employerRequest = {
          name: name,
          email: email,
          password: password,
          description: description, // Include the description field
          street: street,
          city: city,
          state: state,
          zip: zipCode,
          capacity: seatingCapacity,
        };
        await axios.post(
          `${config.baseURL}/register/employer`,
          employerRequest
        );

      enqueueSnackbar( "Successfully registered  ", { variant: "success" });

      }
    } catch (error) {
      enqueueSnackbar(error ? error.message : error, { variant: "error" });
    }

    // try {
    //   const userCredential = await createUserWithEmailAndPassword(
    //     auth,
    //     email,
    //     password
    //   );
    //   const user = userCredential.user;
    //   console.log(user);
    //   await sendEmailVerification(user);

    //   // Send sign up request to the backend based on the userType
    //   if (userType === "employee") {
    //     const employeeRequest = {
    //       name: name,
    //       email: email,
    //       password: password,
    //       employerId: null,
    //       managerEmailId: managerEmail,
    //       title: title, // Include the title field
    //       street: street,
    //       city: city,
    //       state: state,
    //       zip: zipCode,
    //     };
    //     await axios.post(
    //       "http://localhost:8080/register/employee",
    //       employeeRequest
    //     );
    //   } else if (userType === "employer") {
    //     const employerRequest = {
    //       name: name,
    //       email: email,
    //       password: password,
    //       description: description, // Include the description field
    //       street: street,
    //       city: city,
    //       state: state,
    //       zip: zipCode,
    //       capacity: seatingCapacity,
    //     };
    //     await axios.post(
    //       "http://localhost:8080/register/employer",
    //       employerRequest
    //     );
    //   }

    //   alert("Email sent");
    //   await signOut(auth);
    // } catch (error) {
    //   const errorCode = error.code;
    //   const errorMessage = error.message;
    //   enqueueSnackbar(errorMessage, { variant: "error" });
    //   console.log(errorCode, errorMessage);
    // }
  };

  useEffect(() => {
    // Fetch the employer names from the backend
    const fetchEmployerNames = async () => {
      try {
        const response = await axios.get(`${config.baseURL}/employer/all`);
        const data = response.data;
        setEmployerNames(data);
      } catch (error) {
        console.error("Error fetching employer names:", error);
      }
    };

    fetchEmployerNames();
  }, []);

  const handleEmployerChange = (event) => {
    setSelectedEmployer(event.target.value);
  };

  const handleSubmit = () => {
    console.log("Selected employer:", selectedEmployer);
    // TODO: Add functionality to submit the selected employer to the server
  };

  return (
    <Box
      sx={{
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        height: "100vh",
      }}
    >
      <Box
        sx={{
          width: "90%",
          maxWidth: "400px",
          padding: "20px",
          boxShadow: "0px 0px 10px rgba(0, 0, 0, 0.25)",
        }}
      >
        <Typography
          variant="h4"
          sx={{ marginBottom: "20px", textAlign: "center" }}
        >
          Sign up
        </Typography>
        <Box component="form" maxWidth="sm" onSubmit={onSubmit}>
          <div style={{ overflowY: "auto", maxHeight: "500px" }}>
            <TextField
              label="Email address"
              type="email"
              fullWidth
              required
              margin="normal"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
            <TextField
              label="Create password"
              type="password"
              fullWidth
              required
              margin="normal"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />

            <TextField
              label="Name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              margin="normal"
              fullWidth
            />
            <TextField
              label="Street and Number (Optional)"
              value={street}
              onChange={(e) => setStreet(e.target.value)}
              fullWidth
              margin="normal"
            />
            <TextField
              label="City"
              value={city}
              onChange={(e) => setCity(e.target.value)}
              fullWidth
              margin="normal"
            />
            <TextField
              label="State"
              value={state}
              onChange={(e) => setState(e.target.value)}
              fullWidth
              margin="normal"
            />
            <TextField
              label="Zip Code"
              value={zipCode}
              onChange={(e) => setZipCode(e.target.value)}
              fullWidth
              margin="normal"
            />
            <FormControl component="fieldset">
              <Typography sx={{ marginTop: "10px", marginBottom: "10px" }}>
                Select user type:
              </Typography>
              <RadioGroup
                aria-label="user-type"
                name="user-type"
                value={userType}
                onChange={(e) => setUserType(e.target.value)}
              >
                <FormControlLabel
                  value="employee"
                  control={<Radio />}
                  label="Employee"
                />
                <FormControlLabel
                  value="employer"
                  control={<Radio />}
                  label="Employer"
                />
              </RadioGroup>
            </FormControl>
            {userType === "employee" && (
              <>
                <TextField
                  label="Manager Email"
                  type="email"
                  fullWidth
                  margin="normal"
                  value={managerEmail}
                  onChange={(e) => setManagerEmail(e.target.value)}
                />
                <TextField
                  label="Title" // Added title field
                  fullWidth
                  margin="normal"
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                />

                <FormControl sx={{ minWidth: 120 }}>
                  <InputLabel id="employer-select-label">
                    Select Employer
                  </InputLabel>
                  <Select
                    labelId="employer-select-label"
                    id="employer-select"
                    value={selectedEmployer}
                    onChange={handleEmployerChange}
                  >
                    {employerNames.map((name) => (
                      <MenuItem key={name} value={name}>
                        {name.name}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </>
            )}
            {userType === "employer" && (
              <>
                <TextField
                  label="Seating capacity"
                  type="number"
                  fullWidth
                  required
                  margin="normal"
                  value={seatingCapacity}
                  onChange={(e) => setSeatingCapacity(e.target.value)}
                  inputProps={{ min: 3, max: 100 }}
                  error={seatingCapacity < 3 || seatingCapacity > 100}
                  helperText={
                    seatingCapacity < 3 || seatingCapacity > 100
                      ? "Seating capacity must be between 3 and 100."
                      : ""
                  }
                />
                <TextField
                  label="Description" // Added description field
                  fullWidth
                  margin="normal"
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                />
              </>
            )}
          </div>

          <Button
            variant="contained"
            color="primary"
            type="submit"
            fullWidth
            sx={{ marginTop: "20px" }}
          >
            Sign up
          </Button>
        </Box>
        <Typography
          variant="body2"
          sx={{ marginTop: "20px", textAlign: "center" }}
        >
          Already have an account? <Link href="/auth/login">Sign in</Link>
        </Typography>
      </Box>
    </Box>
  );
};

export default Signup;
