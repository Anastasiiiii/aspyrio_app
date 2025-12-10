import { Navigate } from 'react-router-dom';
import Cookies from 'js-cookie';

const PublicRoute = ({ children }) => {
    const token = Cookies.get('token');

    if (token) {
        return <Navigate to="/home" replace />;
    }

    return children;
};

export default PublicRoute;


