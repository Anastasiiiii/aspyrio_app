import axios from 'axios';
import Cookies from 'js-cookie';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const registerNetworkAdmin = async (data) => {
  const response = await api.post('/auth/register-network-admin', data);
  return response.data;
};

export const login = async (data) => {
  const response = await api.post('/auth/login', data);
  return response.data;
};

export const createNetwork = async (data) => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const response = await api.post('/auth/register-fitness-network', data, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.data;
};

export const checkNetwork = async () => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const response = await api.get('/network/check-network', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.data;
};

export const createFitnessCenter = async (data) => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const response = await api.post('/fitness-center/create', data, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.data;
};

export const getFitnessCenters = async () => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const response = await api.get('/fitness-center/list', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.data;
};

export const createFitnessAdmin = async (data) => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const response = await api.post('/fitness-admin/create', data, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.data;
};

export const getFitnessAdmins = async () => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const response = await api.get('/fitness-admin/list', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.data;
};

export const createCoach = async (data) => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const response = await api.post('/coach/create', data, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.data;
};

export const getCoaches = async () => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const response = await api.get('/coach/list', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.data;
};

export const createRegularUser = async (data) => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const response = await api.post('/regular-user/create', data, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.data;
};

export const getRegularUsers = async () => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const response = await api.get('/regular-user/list', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.data;
};

export const createSport = async (data) => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const response = await api.post('/sports/create', data, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.data;
};

export const getSports = async () => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const response = await api.get('/sports/list', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.data;
};

export const getCoachProfile = async () => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  try {
    const response = await api.get('/coach-profile', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    return response.data;
  } catch (error) {
    if (error.response && error.response.status === 404) {
      return null; // Profile doesn't exist yet
    }
    throw error;
  }
};

export const updateCoachProfile = async (data) => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const response = await api.put('/coach-profile', data, {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });
  return response.data;
};

export const uploadCoachPhoto = async (file) => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const formData = new FormData();
  formData.append('file', file);
  
  const response = await api.post('/coach-profile/upload-photo', formData, {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'multipart/form-data'
    }
  });
  return response.data;
};

export const getTrainingSlots = async (startDate, endDate) => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const params = {};
  if (startDate) {
    params.startDate = startDate.toISOString();
  }
  if (endDate) {
    params.endDate = endDate.toISOString();
  }
  
  const response = await api.get('/training-slots', {
    headers: {
      'Authorization': `Bearer ${token}`
    },
    params
  });
  return response.data;
};

export const createTrainingSlot = async (data) => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const response = await api.post('/training-slots', data, {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });
  return response.data;
};

export const deleteTrainingSlot = async (id) => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const response = await api.delete(`/training-slots/${id}`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.data;
};

export const getCoachesWithSports = async () => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const response = await api.get('/group-training-slots/coaches', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.data;
};

export const getCoachesForUser = async () => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const response = await api.get('/group-training-slots/coaches-for-user', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.data;
};

export const createGroupTrainingSlot = async (data) => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const response = await api.post('/group-training-slots', data, {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });
  return response.data;
};

export const approveRejectTrainingSlot = async (data) => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const response = await api.post('/group-training-slots/approve-reject', data, {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });
  return response.data;
};

export const getStudios = async () => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const response = await api.get('/studios', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.data;
};

export const createStudio = async (data) => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const response = await api.post('/studios', data, {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });
  return response.data;
};

export const getTrainingSlotRequests = async () => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const response = await api.get('/group-training-slots/requests', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.data;
};

export const getGroupTrainingSlotsForCalendar = async (startDate, endDate) => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const params = {};
  if (startDate) {
    params.startDate = startDate.toISOString();
  }
  if (endDate) {
    params.endDate = endDate.toISOString();
  }
  
  const response = await api.get('/group-training-slots/calendar', {
    headers: {
      'Authorization': `Bearer ${token}`
    },
    params
  });
  return response.data;
};

export const getAvailableGroupTrainingSlots = async (startDate, endDate) => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const params = {};
  if (startDate) {
    params.startDate = startDate.toISOString();
  }
  if (endDate) {
    params.endDate = endDate.toISOString();
  }
  
  const response = await api.get('/group-training-slots/available', {
    headers: {
      'Authorization': `Bearer ${token}`
    },
    params
  });
  return response.data;
};

export const bookGroupTrainingSlot = async (slotId) => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const response = await api.post('/group-training-slots/book', {
    slotId: slotId
  }, {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });
  return response.data;
};

export const getUserBookings = async (startDate, endDate) => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const params = {};
  if (startDate) {
    params.startDate = startDate.toISOString();
  }
  if (endDate) {
    params.endDate = endDate.toISOString();
  }
  
  const response = await api.get('/group-training-slots/my-bookings', {
    headers: {
      'Authorization': `Bearer ${token}`
    },
    params
  });
  return response.data;
};

export const createIndividualTrainingRequest = async (data) => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const response = await api.post('/individual-training-requests', data, {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });
  return response.data;
};

export const getAllTrainingSlotRequests = async () => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const response = await api.get('/group-training-slots/all-requests', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.data;
};

export const approveRejectIndividualTrainingRequest = async (data) => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const response = await api.post('/individual-training-requests/approve-reject', data, {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });
  return response.data;
};

export const getUserProfile = async () => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  try {
    const response = await api.get('/user-profile', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    return response.data;
  } catch (error) {
    if (error.response && error.response.status === 404) {
      return null; // Profile doesn't exist yet
    }
    throw error;
  }
};

export const updateUserProfile = async (data) => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const response = await api.put('/user-profile', data, {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });
  return response.data;
};

export const uploadUserPhoto = async (file) => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const formData = new FormData();
  formData.append('file', file);
  
  const response = await api.post('/user-profile/upload-photo', formData, {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'multipart/form-data'
    }
  });
  return response.data;
};

export const generatePass = async () => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const response = await api.post('/passes/generate', {}, {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });
  return response.data;
};

export const uploadReport = async (file) => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const formData = new FormData();
  formData.append('file', file);
  
  const response = await api.post('/reports/upload', formData, {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'multipart/form-data'
    }
  });
  return response.data;
};

export const getReports = async () => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const response = await api.get('/reports', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.data;
};

export const getReportsForNetworkAdmin = async () => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const response = await api.get('/reports/network-admin', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.data;
};

export const summarizeReport = async (reportId) => {
  const token = Cookies.get('token');
  
  if (!token) {
    throw new Error('No authentication token found');
  }
  
  const response = await api.post('/reports/summarize', { reportId }, {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });
  return response.data;
};
