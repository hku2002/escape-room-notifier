import { FC, useState } from 'react';
import { createEarthEscapeReservation } from '../services/reservationApi';
import './EarthEscapeReservation.css';

interface ReservationForm {
  branch: string;
  theme: string;
  date: string;
  time: string;
  name: string;
  phone: string;
  people: string;
  paymentMethod: string;
  policy: boolean;
}

const EarthEscapeReservation: FC = () => {
  const [form, setForm] = useState<ReservationForm>({
    branch: '2', // 홍대어드벤처점
    theme: '',
    date: '',
    time: '',
    name: '',
    phone: '',
    people: '2',
    paymentMethod: '21', // 가상계좌
    policy: false,
  });

  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  const branches = [
    { id: '1', name: '대구점' },
    { id: '2', name: '홍대어드벤처점' },
    { id: '4', name: '홍대라스트시티점' },
  ];

  // 지점별 테마
  const themes = [
    // 대구점 (branch: 1)
    { id: '20', name: '잉카', branch: '1' },
    { id: '11', name: '우리 아빠', branch: '1' },
    { id: '6', name: '사명 : 투쟁의 노래', branch: '1' },
    { id: '5', name: '펭귄키우기', branch: '1' },
    { id: '3', name: '너의 겨울은 가고, 봄은 온다', branch: '1' },
    { id: '2', name: '만월 <<꿈을 훔치는 요괴>>', branch: '1' },
    { id: '1', name: '단디해라', branch: '1' },
    // 홍대어드벤처점 (branch: 2)
    { id: '25', name: 'PINOCCHIO(피노키오)', branch: '2' },
    { id: '23', name: '잔향', branch: '2' },
    { id: '18', name: '아몬 : 새벽을 여는 소년', branch: '2' },
    { id: '17', name: '퀘스트 : 여정의 시작', branch: '2' },
    { id: '9', name: '지난날을 잊었다', branch: '2' },
    { id: '8', name: '미스터리', branch: '2' },
  ];

  const availableTimes = [
    '10:00',
    '11:30',
    '13:00',
    '14:30',
    '16:00',
    '17:30',
    '19:00',
    '20:30',
    '22:00',
  ];

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!form.policy) {
      setMessage({ type: 'error', text: '개인정보 처리방침에 동의해주세요.' });
      return;
    }

    try {
      setLoading(true);
      setMessage(null);

      const response = await createEarthEscapeReservation(form);

      if (response.success) {
        setMessage({ type: 'success', text: '예약이 완료되었습니다!' });
        // 폼 초기화
        setForm({
          branch: '2',
          theme: '',
          date: '',
          time: '',
          name: '',
          phone: '',
          people: '2',
          paymentMethod: '21',
          policy: false,
        });
      } else {
        setMessage({ type: 'error', text: response.message || '예약에 실패했습니다.' });
      }
    } catch (error: any) {
      setMessage({
        type: 'error',
        text: error.response?.data?.message || '예약 중 오류가 발생했습니다.'
      });
    } finally {
      setLoading(false);
    }
  };

  // 전화번호 자동 하이픈 포맷팅
  const formatPhoneNumber = (value: string): string => {
    // 숫자만 추출
    const numbers = value.replace(/[^0-9]/g, '');

    // 길이에 따라 포맷팅
    if (numbers.length <= 3) {
      return numbers;
    } else if (numbers.length <= 7) {
      return `${numbers.slice(0, 3)}-${numbers.slice(3)}`;
    } else if (numbers.length <= 10) {
      return `${numbers.slice(0, 3)}-${numbers.slice(3, 6)}-${numbers.slice(6)}`;
    } else {
      return `${numbers.slice(0, 3)}-${numbers.slice(3, 7)}-${numbers.slice(7, 11)}`;
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value, type } = e.target;

    let newValue = value;

    // 전화번호 필드인 경우 자동 포맷팅
    if (name === 'phone') {
      newValue = formatPhoneNumber(value);
    }

    setForm(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? (e.target as HTMLInputElement).checked : newValue,
    }));
  };

  // 선택된 지점에 해당하는 테마만 필터링
  const availableThemes = themes.filter(theme => theme.branch === form.branch);

  return (
    <div className="earth-reservation-container">
      <header className="header">
        <h1 className="title">지구별 방탈출 예약</h1>
      </header>

      <main className="main-content">
        <form className="reservation-form" onSubmit={handleSubmit}>
          <div className="form-section">
            <h2 className="section-title">예약 정보</h2>

            <div className="form-group">
              <label htmlFor="branch">지점 *</label>
              <select
                id="branch"
                name="branch"
                value={form.branch}
                onChange={handleChange}
                required
              >
                {branches.map(branch => (
                  <option key={branch.id} value={branch.id}>
                    {branch.name}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="theme">테마 *</label>
              <select
                id="theme"
                name="theme"
                value={form.theme}
                onChange={handleChange}
                required
              >
                <option value="">테마를 선택하세요</option>
                {availableThemes.map(theme => (
                  <option key={theme.id} value={theme.id}>
                    {theme.name}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="date">예약 날짜 *</label>
              <input
                type="date"
                id="date"
                name="date"
                value={form.date}
                onChange={handleChange}
                required
                min={new Date().toISOString().split('T')[0]}
              />
            </div>

            <div className="form-group">
              <label htmlFor="time">예약 시간 *</label>
              <select
                id="time"
                name="time"
                value={form.time}
                onChange={handleChange}
                required
              >
                <option value="">시간을 선택하세요</option>
                {availableTimes.map(time => (
                  <option key={time} value={time}>
                    {time}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="people">인원 수 *</label>
              <select
                id="people"
                name="people"
                value={form.people}
                onChange={handleChange}
                required
              >
                <option value="2">2명</option>
                <option value="3">3명</option>
                <option value="4">4명</option>
                <option value="5">5명</option>
                <option value="6">6명</option>
              </select>
            </div>
          </div>

          <div className="form-section">
            <h2 className="section-title">예약자 정보</h2>

            <div className="form-group">
              <label htmlFor="name">이름 *</label>
              <input
                type="text"
                id="name"
                name="name"
                value={form.name}
                onChange={handleChange}
                placeholder="홍길동"
                required
                maxLength={10}
              />
            </div>

            <div className="form-group">
              <label htmlFor="phone">연락처 *</label>
              <input
                type="tel"
                id="phone"
                name="phone"
                value={form.phone}
                onChange={handleChange}
                placeholder="01012345678 또는 010-1234-5678"
                required
                maxLength={13}
              />
              <p className="form-hint">숫자만 입력하셔도 자동으로 하이픈이 추가됩니다</p>
            </div>
          </div>

          <div className="form-section">
            <h2 className="section-title">결제 및 동의</h2>

            <div className="form-group">
              <label>결제 방식</label>
              <div className="payment-info">
                가상계좌
              </div>
            </div>

            <div className="form-group checkbox-group">
              <label className="checkbox-label">
                <input
                  type="checkbox"
                  name="policy"
                  checked={form.policy}
                  onChange={handleChange}
                  required
                />
                <span>개인정보 처리방침 및 주의사항에 동의합니다 *</span>
              </label>
            </div>

            <div className="notice-box">
              <h3>주의사항</h3>
              <ul>
                <li>예약 확인 문자를 수신하지 못한 경우는 예약이 되지 않았거나 전화번호가 잘못 입력된 것입니다. 매장으로 연락 부탁드립니다.</li>
                <li>예약시간은 테마 입장시간입니다. 반드시 15분 전에 도착해 주세요.</li>
                <li>입장시간에 도착할 경우 테마 이용 시간이 차감될 수 있습니다.</li>
                <li>예약 실수로 발생한 문제에 대해서는 책임지지 않습니다.</li>
                <li>당일 예약 취소로 인한 환불과 당일 예약 시간 변경은 불가능합니다.</li>
                <li>예약 취소와 변경은 테마 이용 전날까지만 가능합니다.</li>
                <li>예약 취소 시 환불은 영업일 기준 3~5일 정도 소요됩니다.</li>
                <li>플레이시간이 20분 지날 때까지 연락이 닿지 않고 미방문 시 노쇼로 판단하여 예약이 취소될 수 있습니다. (이 경우 당일 취소로 인하여 환불이 불가합니다.)</li>
                <li>외부 음식물 반입 및 취식이 불가능합니다.</li>
                <li>음주 고객님은 음주량에 상관없이 입장이 불가합니다.</li>
                <li>13세 (초등학교 6학년) 이하는 안전상의 이유로 보호자 동반 입장이 필수입니다. (보호자 : 고등학생 이상)</li>
                <li>매장 내 주차장이 따로 존재하지 않습니다. 대중교통 이용 부탁드립니다.</li>
                <li>연무(포그)가 많은 구간이 있으니 예약시 참고 바랍니다.</li>
              </ul>
            </div>
          </div>

          {message && (
            <div className={`message ${message.type}`}>
              {message.text}
            </div>
          )}

          <div className="form-actions">
            <button
              type="submit"
              className="btn-submit"
              disabled={loading}
            >
              {loading ? '예약 중...' : '예약하기'}
            </button>
          </div>
        </form>
      </main>
    </div>
  );
};

export default EarthEscapeReservation;
