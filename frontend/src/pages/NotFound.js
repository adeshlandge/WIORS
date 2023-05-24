import React from "react";
import ArrowLeftIcon from "@heroicons/react/24/solid/ArrowLeftIcon";
import { Box, Button, Container, SvgIcon, Typography } from "@mui/material";

const NotFound = () => (
  <>


    <Box
      component="main"
      sx={{
        m:20,
        alignItems: "center",
        display: "flex",
        flexGrow: 1,
        minHeight: "100%",
      }}
    >
      <Container maxWidth="md">
        <Box
          sx={{
            alignItems: "center",
            display: "flex",
            flexDirection: "column",
          }}
        >
          <Box
            sx={{
              mb: 3,
              textAlign: "center",
            }}
          >
            <img
              alt="Page not found"
              src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT0bx_H3kzFgRLsiZQuIsF9-vu1V8rAv-OBog&usqp=CAU"
              style={{
                display: "inline-block",
                maxWidth: "100%",
                width: 400,
              }}
            />
          </Box>
          <Typography align="center" sx={{ mb: 3 }} variant="h3">
            404: The page you are looking for isn’t here
          </Typography>
          <Typography align="center" color="text.secondary" variant="body1">
            You either tried some shady route or you came here by mistake.
            Whichever it is, try using the navigation
          </Typography>
          <Button
            href="/"
            startIcon={
              <SvgIcon fontSize="small">
                <ArrowLeftIcon />
              </SvgIcon>
            }
            sx={{ mt: 3 }}
            variant="contained"
          >
            Go back to dashboard
          </Button>
        </Box>
      </Container>
    </Box>
  </>
);

export default NotFound;
