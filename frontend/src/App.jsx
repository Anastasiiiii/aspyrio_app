import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import NetworkAdminRegistration from './modules/networkAdminRegistration/NetworkAdminRegistration'
import NetworkAdminMainPage from './modules/networkAdminMainPage/NetworkAdminMainPage'
import FitnessAdminMainPage from './modules/fitnessAdminMainPage/FitnessAdminMainPage'
import CoachMainPage from './modules/coachMainPage/CoachMainPage'
import UserMainPage from './modules/userMainPage/UserMainPage'
import LoginPage from './modules/loginPage/LoginPage'
import ProtectedRoute from './components/ProtectedRoute'
import PublicRoute from './components/PublicRoute'

function App() {
  return (
    <Router>
      <Routes>
        <Route 
          path="/" 
          element={
            <PublicRoute>
              <NetworkAdminRegistration />
            </PublicRoute>
          } 
        />
        <Route 
          path="/login" 
          element={
            <PublicRoute>
              <LoginPage />
            </PublicRoute>
          } 
        />
        <Route 
          path="/network-admin" 
          element={
            <ProtectedRoute>
              <NetworkAdminMainPage />
            </ProtectedRoute>
          } 
        />
        <Route 
          path="/fitness-admin" 
          element={
            <ProtectedRoute>
              <FitnessAdminMainPage />
            </ProtectedRoute>
          } 
        />
        <Route 
          path="/coach" 
          element={
            <ProtectedRoute>
              <CoachMainPage />
            </ProtectedRoute>
          } 
        />
        <Route 
          path="/user" 
          element={
            <ProtectedRoute>
              <UserMainPage />
            </ProtectedRoute>
          } 
        />
        <Route 
          path="/home" 
          element={
            <ProtectedRoute>
              <NetworkAdminMainPage />
            </ProtectedRoute>
          } 
        />
      </Routes>
    </Router>
  )
}

export default App

