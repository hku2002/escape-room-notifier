import { FC } from 'react';
import { UserAlert } from '../types/alert';
import './AlertCard.css';

interface AlertCardProps {
  alert: UserAlert;
}

const AlertCard: FC<AlertCardProps> = ({ alert }) => {
  const { isActive, theme, dateStart, dateEnd, preferredTimes, numPeople } = alert;

  return (
    <div className={`alert-card ${isActive ? 'active' : 'inactive'}`}>
      <div className="alert-header">
        <span className="status-icon">{isActive ? '🔔' : '🔕'}</span>
        <span className="status-text">{isActive ? '활성' : '비활성'}</span>
      </div>

      <h3 className="theme-name">{theme.themeName}</h3>
      <p className="cafe-info">
        {theme.cafeName} {theme.branchName}
      </p>

      <div className="alert-details">
        <div className="detail-row">
          <span className="icon">📅</span>
          <span className="text">
            {dateStart} ~ {dateEnd}
          </span>
        </div>

        {preferredTimes.length > 0 && (
          <div className="detail-row">
            <span className="icon">⏰</span>
            <span className="text">{preferredTimes.join(', ')}</span>
          </div>
        )}

        <div className="detail-row">
          <span className="icon">👥</span>
          <span className="text">{numPeople}명</span>
        </div>
      </div>

      <div className="alert-actions">
        <button className="btn-secondary">상세보기</button>
        <button className="btn-danger">삭제</button>
      </div>
    </div>
  );
};

export default AlertCard;
