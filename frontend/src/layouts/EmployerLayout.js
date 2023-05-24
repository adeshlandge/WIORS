import React from 'react'
import { NavLink, Outlet } from 'react-router-dom';
import Box from "@mui/material/Box";
import Drawer from "@mui/material/Drawer";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";
import Divider from "@mui/material/Divider";
import HomeIcon from "@mui/icons-material/Home";
import DashboardIcon from "@mui/icons-material/Dashboard";
import SettingsIcon from "@mui/icons-material/Settings";
import { Link, useLocation } from "react-router-dom";

const drawerWidth = 280;

const EmployerLayout = () => {
  return (
    <div>
    <Box sx={{ display: "flex" }}>
    <Drawer
        sx={{
          width: drawerWidth,
          flexShrink: 0,
          "& .MuiDrawer-paper": {
            width: drawerWidth,
            boxSizing: "border-box",
            backgroundColor: 'neutral.800',
            color: 'common.white',
          },
         
        }}
        
        variant="permanent"
        anchor="left"
      >
        <Toolbar />
        <Box sx={{ overflow: "auto" }}>
          <List>
            <ListItem
              button
              component={Link}
              to="/employer/home"
              // selected={isActive("/")}
            >
              <ListItemIcon>
                <HomeIcon />
              </ListItemIcon>
              <ListItemText primary="Home" />
            </ListItem>
            {/* <ListItem
              button
              component={Link}
              to="/employer/dashboard"
              // selected={isActive("/dashboard")}
            >
              <ListItemIcon>
                <DashboardIcon />
              </ListItemIcon>
              <ListItemText primary="Dashboard" />
            </ListItem> */}
            <ListItem
              button
              component={Link}
              to="/employer/officeattendance"
              // selected={isActive("/settings")}
            >
              <ListItemIcon>
                <SettingsIcon />
              </ListItemIcon>
              <ListItemText primary="Office Attendance" />
            </ListItem>
            <ListItem
              button
              component={Link}
              to="/employer/bulkreservation"
              // selected={isActive("/settings")}
            >
              <ListItemIcon>
                <SettingsIcon />
              </ListItemIcon>
              <ListItemText primary="Bulk Reservation" />
            </ListItem>

            <ListItem
              button
              component={Link}
              to="/employer/bulkemployeecreation"
              // selected={isActive("/settings")}
            >
              <ListItemIcon>
                <SettingsIcon />
              </ListItemIcon>
              <ListItemText primary="Bulk Employee Creation" />
            </ListItem>
            <ListItem
              button
              to="/employer/home"
              component={Link}
              // selected={isActive("/settings")}
            >
              <ListItemIcon>
                <SettingsIcon />
              </ListItemIcon>
              <ListItemText primary="Logout"  />
            </ListItem>
          </List>

          
        </Box>
      </Drawer>
      <Box component="main" sx={{ flexGrow: 1, p: 3 }}>
        <Toolbar />
        <Outlet />
      </Box>
    </Box>
    
    </div>
   
  )
}

export default EmployerLayout